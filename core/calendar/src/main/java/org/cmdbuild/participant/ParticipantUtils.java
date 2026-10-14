/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.participant;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import static com.google.common.collect.ImmutableList.toImmutableList;
import com.google.common.collect.Streams;
import static java.lang.String.format;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.apache.commons.lang3.StringUtils.trim;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.auth.role.Participant;
import org.cmdbuild.auth.role.ParticipantImpl;
import org.cmdbuild.auth.role.ParticipantType;
import static org.cmdbuild.auth.role.ParticipantType.PT_ROLE;
import static org.cmdbuild.auth.role.ParticipantType.PT_USER;
import org.cmdbuild.auth.user.LoginUserInfo;
import static org.cmdbuild.participant.ParticipantHeader.GROUP;
import static org.cmdbuild.participant.ParticipantHeader.USER;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class ParticipantUtils {

    private final static Map<ParticipantHeader, ParticipantType> MAP_HEADER_TO_TYPE = map(USER, PT_USER, GROUP, PT_ROLE);

    public static ParticipantHelper buildParticipants() {
        return new ParticipantHelperImpl();
    }

    private static String buildToken(ParticipantHeader header, String key) {
        return format("%s.%s", serializeEnum(checkNotNull(header)), checkNotBlank(key));
    }

    public static List<String> checkParticipants(Collection<String> participants) {
        parseParticipants(participants);
        return ImmutableList.copyOf(participants);
    }

    public static List<Participant> parseParticipants(Collection<String> participants) {
        return participants.stream().map(p -> {
            try {
                checkNotBlank(p);
                Matcher matcher = Pattern.compile("^([^.]+)[.](.+)$").matcher(trim(p));
                checkArgument(matcher.matches(), "invalid token syntax");
                ParticipantHeader header = parseEnum(matcher.group(1), ParticipantHeader.class);
                String key = checkNotBlank(matcher.group(2));
                return Pair.of(header, key);
            } catch (Exception ex) {
                throw runtime(ex, "invalid participant token =< %s >", p);
            }
        }).map(p -> new ParticipantImpl(checkNotNull(MAP_HEADER_TO_TYPE.get(p.getKey())), toLong(p.getRight()))).collect(toImmutableList());
    }

    private static class ParticipantHelperImpl implements ParticipantHelper {

        private final Set<String> participants = set();

        @Override
        public ParticipantHelper addUsers(LoginUserInfo... users) {
            Arrays.stream(users).map(LoginUserInfo::getId).forEach(u -> addToken(ParticipantHeader.USER, Long.toString(u)));
            return this;
        }

        @Override
        public ParticipantHelper addUsers(long... userIds) {
            Arrays.stream(userIds).forEach(u -> addToken(ParticipantHeader.USER, Long.toString(u)));
            return this;
        }

        @Override
        public ParticipantHelper addRoles(long... groupIds) {
            Arrays.stream(groupIds).forEach(g -> addToken(ParticipantHeader.GROUP, Long.toString(g)));
            return this;
        }

        @Override
        public ParticipantHelper addRoles(Iterable<Long> groupIds) {
            Streams.stream(groupIds).forEach(g -> addToken(ParticipantHeader.GROUP, Long.toString(g)));
            return this;
        }

        private void addToken(ParticipantHeader header, String key) {
            participants.add(buildToken(header, key));
        }

        @Override
        public List<String> toParticipants() {
            return ImmutableList.copyOf(participants);
        }

    }

}
