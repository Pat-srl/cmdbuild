/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.crypto;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Iterables.getLast;
import static com.google.common.collect.Iterators.limit;
import jakarta.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import static java.util.Arrays.asList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.utils.io.CmIoUtils.copy;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;

public class MagicUtils {

    public static MagicUtilsHelper helper(String magic, Integer... magicPos) {
        return helper(magic, asList(magicPos));
    }

    public static MagicUtilsHelper helper(String magic, List<Integer> magicPos) {
        return new MagicUtilsHelper(Splitter.fixedLength(1).splitToList(magic), magicPos);//TODO check charset
    }

    public static MagicUtilsHelper helper(byte[] magic, Integer... magicPos) {
        return helper(magic, asList(magicPos));
    }

    public static MagicUtilsHelper helper(byte[] magic, List<Integer> magicPos) {
        return new MagicUtilsHelper(list(ArrayUtils.toObject(magic)), magicPos);
    }

    public static class MagicUtilsHelper {

        private final List<Character> magicChars;
        private final List<Byte> magicBytes;
        private final List<Integer> magicPos;
        private final int magicSize;

        public MagicUtilsHelper(List<?> magic, List<Integer> magicPos) {
            this.magicChars = magic.stream().map(c -> c instanceof Byte ? Character.valueOf((char) (byte) (Byte) c) : ((String) c).charAt(0)).collect(toImmutableList());
            this.magicBytes = magic.stream().map(c -> c instanceof Byte ? (Byte) c : ((String) c).getBytes(StandardCharsets.US_ASCII)[0]).collect(toImmutableList());
            this.magicPos = ImmutableList.copyOf(magicPos);
            magicSize = magicPos.size();
            checkArgument(magicSize > 0 && magicSize == magicChars.size() && magicSize == magicBytes.size());
            checkArgument(magicPos.stream().allMatch(p -> p != null && p >= 0));
            checkArgument(set(magicPos).size() == magicPos.size());
        }

        public boolean canEmbedMagic(String str) {
            return str.length() > getLast(magicPos);
        }

        public String embedMagicIfPossible(String str) {
            if (canEmbedMagic(str)) {
                str = embedMagic(str);
            }
            return str;
        }

        public String embedMagic(String str) {
            checkArgument(canEmbedMagic(str));
            StringBuilder sb = new StringBuilder();
            int i, j = 0, k = 0;
            Iterator<Integer> iterator = magicPos.iterator();
            Iterator<Character> chars = magicChars.iterator();
            while (iterator.hasNext()) {
                i = j;
                j = iterator.next() - k;
                k++;
                sb.append(str.substring(i, j));
                sb.append(chars.next());
            }
            sb.append(str.substring(j));
            String res = sb.toString();
            checkArgument(res.length() == str.length() + magicSize);
            checkArgument(hasMagic(res));
            return res;
        }

        public byte[] embedMagic(byte[] data) {
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ByteArrayInputStream in = new ByteArrayInputStream(data);
                DataOutputStream out = new DataOutputStream(byteArrayOutputStream);
                int offset = 0;
                byte[] buffer = new byte[getLast(magicPos) + 1];
                Iterator<Integer> iterator = magicPos.iterator();
                Iterator<Byte> bytes = magicBytes.iterator();
                while (iterator.hasNext()) {
                    int next = iterator.next(), skip = next - offset;
                    int count = in.read(buffer, 0, skip);
                    if (count != -1) {
                        out.write(buffer, 0, count);
                    }
                    out.writeByte(bytes.next());
                    offset += count + 1;
                }
                copy(in, out);
                byte[] res = byteArrayOutputStream.toByteArray();
                checkArgument(res.length == data.length + magicSize);
                checkArgument(hasMagic(res));
                return res;
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        public boolean hasMagic(@Nullable String str) {
            if (isBlank(str) || str.length() <= getLast(magicPos)) {//TODO improve this
                return false;
            } else {
                for (int i = 0; i < magicSize; i++) {
                    char c = magicChars.get(i);
                    int pos = magicPos.get(i);
                    if (c != str.charAt(pos)) {
                        return false;
                    }
                }
                return true;
            }
        }

        public boolean hasMagic(@Nullable InputStream in) {
            if (in == null) {
                return false;
            } else {
                try {
                    int size = getLast(magicPos) + 1;
                    byte[] buffer = new byte[size];
                    int count = in.read(buffer);
                    return count >= magicSize && hasMagic(Arrays.copyOfRange(buffer, 0, count));
                } catch (IOException ex) {
                    throw runtime(ex);
                }
            }
        }

        public boolean hasMagic(@Nullable byte[] data) {
            if (data == null || data.length < magicSize) {
                return false;
            } else {
                int len = data.length;
                for (int i = magicSize - 1; i >= 0; i--) {
                    byte c = magicBytes.get(i);
                    int pos = magicPos.get(i);
                    if (pos < len) {
                        if (c != data[pos]) {
                            return false;
                        }
                    } else {
                        if (c != data[len - 1]) {
                            return false;
                        } else {
                            len--;
                        }
                    }
                }
                return true;
            }
        }

        public String stripMagic(String str) {
            StringBuilder sb = new StringBuilder();
            int i, j = 0;
            Iterator<Integer> iterator = magicPos.iterator();
            while (iterator.hasNext()) {
                i = j;
                j = iterator.next();
                sb.append(str.substring(i, j));
                j++;
            }
            sb.append(str.substring(j));
            String res = sb.toString();
            checkArgument(res.length() == str.length() - magicSize);
            return res;
        }

        public byte[] stripMagic(byte[] data) {
            checkArgument(hasMagic(data));
            int strip = 0;
            for (int i = magicSize - 1; i >= 0; i--) {
                if (data.length - strip > magicPos.get(i)) {
                    break;
                } else {
                    strip++;
                }
            }
            ByteArrayInputStream in = new ByteArrayInputStream(data, 0, data.length - strip);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offset = 0;
            Iterator<Integer> iterator = limit(magicPos.iterator(), magicSize - strip);
            byte[] buffer = new byte[getLast(magicPos) + 1];
            while (iterator.hasNext()) {
                int next = iterator.next(), skip = next - offset;
                in.read(buffer, 0, skip);
                in.read();
                out.write(buffer, 0, skip);
                offset += skip + 1;
            }
            copy(in, out);
            byte[] res = out.toByteArray();
            checkArgument(res.length == data.length - magicSize);
            return res;
        }

        public OutputStream addMagicOnClose(OutputStream inner) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            return new FilterOutputStream(buffer) {

                @Override
                public void write(byte[] b, int off, int len) throws IOException {
                    super.write(b, off, len);
                    checkMagic(false);
                }

                @Override
                public void write(byte[] b) throws IOException {
                    super.write(b);
                    checkMagic(false);
                }

                @Override
                public void write(int b) throws IOException {
                    super.write(b);
                    checkMagic(false);
                }

                @Override
                public void close() throws IOException {
                    checkMagic(true);
                    super.close();
                } 

                private void checkMagic(boolean force) throws IOException {
                    if (this.out == buffer && (force || buffer.size() > getLast(magicPos))) {
                        inner.write(embedMagic(buffer.toByteArray()));
                        this.out = inner;
                    }
                }

            };
        }

    }

}
