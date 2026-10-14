/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.encode.test;

import com.google.common.base.Stopwatch;
import static com.google.common.io.ByteStreams.toByteArray;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import static org.cmdbuild.utils.date.CmDateUtils.toUserDuration;
import static org.cmdbuild.utils.encode.CmPackUtils.isPacked;
import static org.cmdbuild.utils.encode.CmPackUtils.unpackBytes;
import static org.cmdbuild.utils.encode.CmPackUtils.unpackBytesIfPacked;
import static org.cmdbuild.utils.encode.CmPackUtils.unpackBytesIfPackedOrBase64;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import static org.cmdbuild.utils.encode.CmPackUtils.unpackIfPacked;
import static org.cmdbuild.utils.encode.CmPackUtils.unpackIfPackedOrBase64;
import static org.cmdbuild.utils.encode.CmPackUtils.unpack;
import static org.cmdbuild.utils.encode.CmPackUtils.pax;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.junit.Assert.assertArrayEquals;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.cmdbuild.utils.encode.CmPackUtils.lpack;
import static org.cmdbuild.utils.encode.CmPackUtils.lpack;

public class PackUtilsTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void testUnpackStability1() {
        assertTrue(isPacked("packe3ifcn1m9n30h"));
        assertEquals("", unpack("packe3ifcn1m9n30h"));
        assertTrue(isPacked("packmd90jix757odes63ju5fyi800k68l9nhzyias5cpc8jbq41glzjqbitkfvvmb87o9011kpdix595kscbp6dqqg1vx5m60j6qf7"));
        assertEquals("fnwvow5v 45vg 45hg9oe %^#@$%^!& +)_+:}:>][;/[;\n\n\rHELLO!", unpack("packmd90jix757odes63ju5fyi800k68l9nhzyias5cpc8jbq41glzjqbitkfvvmb87o9011kpdix595kscbp6dqqg1vx5m60j6qf7"));
    }

    @Test
    public void testUnpackStability2() {
        assertTrue(isPacked("packe3ifcn1m9n30hkcap"));
        assertEquals("", unpack("packe3ifcn1m9n30hkcap"));
        assertTrue(isPacked("packmd90jix757odes63ju5fyi800k68l9nhzyias5cpc8jbq41glzjqbitkfvvmb87o9011kpdix595kscbp6dqqg1vx5m60j6qf7kcap"));
        assertEquals("fnwvow5v 45vg 45hg9oe %^#@$%^!& +)_+:}:>][;/[;\n\n\rHELLO!", unpack("packmd90jix757odes63ju5fyi800k68l9nhzyias5cpc8jbq41glzjqbitkfvvmb87o9011kpdix595kscbp6dqqg1vx5m60j6qf7kcap"));

        assertTrue(isPacked("  packmd90jix757odes63ju5fyi800k68l9nhzyias5cpc8jbq41glzjqbitkfvvmb87o9011kpdix595kscbp6dqqg1vx5m60j6qf7kcap"));
        assertEquals("fnwvow5v 45vg 45hg9oe %^#@$%^!& +)_+:}:>][;/[;\n\n\rHELLO!", unpack("  packmd90jix757odes63ju5fyi800k68l9nhzyias5cpc8jbq41glzjqbitkfvvmb87o9011kpdix595kscbp6dqqg1vx5m60j6qf7kcap"));

        assertTrue(isPacked(" packmd90jix757odes63ju5fyi800k68l9nhzyias5cpc8jbq41glzjqbitkfvvmb87o9011kpdix595kscbp6dqqg1vx5m60j6qf7kcap  "));
        assertEquals("fnwvow5v 45vg 45hg9oe %^#@$%^!& +)_+:}:>][;/[;\n\n\rHELLO!", unpack(" packmd90jix757odes63ju5fyi800k68l9nhzyias5cpc8jbq41glzjqbitkfvvmb87o9011kpdix595kscbp6dqqg1vx5m60j6qf7kcap  "));
    }

    @Test
    public void testLegacyPackUtils1() {
        String value = lpack("");
        assertEquals("packe3ifcn1m9n30hkcap", value);
        assertTrue(isPacked(value));
        assertEquals("", unpack(value));
    }

    @Test
    public void testLegacyPackUtils2() {
        String value = "fnwvow5v 45vg 45hg9oe %^#@$%^!& +)_+:}:>][;/[;\n\n\rHELLO!",
                packed = lpack(value);
        assertEquals("packmd90jix757odes63ju5fyi800k68l9nhzyias5cpc8jbq41glzjqbitkfvvmb87o9011kpdix595kscbp6dqqg1vx5m60j6qf7kcap", packed);
        assertTrue(isPacked(packed));
        assertEquals(value, unpack(packed));
        assertEquals(value, unpackIfPacked(packed));
        assertEquals(value, unpackIfPackedOrBase64(packed));
    }

    @Test
    public void testLegacyPackUtils3() {
        String value = "hi                                                     enable                                                                                          deflate                     plz",
                packed = lpack(value);
        assertEquals("pack5it5xucos67lnyeaepsxue4cgg6lb0eemtk68e2jg25e5mfblumjex832zjygkak09kcap", packed);
        assertTrue(isPacked(packed));
        assertEquals(value, unpack(packed));
        assertEquals(value, unpackIfPacked(packed));
        assertEquals(value, unpackIfPackedOrBase64(packed));
    }

    @Test
    public void testLegacyPackUtils4() throws IOException {
        byte[] data = toByteArray(getClass().getResourceAsStream("/org/cmdbuild/utils/encode/test/data.raw"));
        String packed = lpack(data);
        assertEquals("pack2ojg99ijzrnvi3r34aqmkgoya73zmppi5b7ywvs4cv2gm8pemuslywca9fqf9mzt9slcyrrp8qza0qwntqe2cky6wi7yk867bbarufoisifyf6j2rm9vxy3psgyj1sfp90audfk7cjmticbd4i71vylkjrc28wqzoj30abru8sgakk776a9o95hrsr9urb4d8n4jmwe7sikxyatrslxbs4o9kr2jby90v605sk5eqha28xmhpn8p5t9g6wwn9j2gij4klyfhtn1cpf5ejlpyfek4tknyyxomk0wmnx6s9f3f5r9gbflm2ixrc1x5bh8o28lm8smjfx7oybjinfns3d97lu62sx4whor5rf8ba50igluebia8eyvk6orxbjvk32bt1l3x8rj3o3q4j09wdd79233z77k356c3roc5dydi04szs0o0jkwrbpqx1ap15w8c6xltzqh77jkyyejsq66p91a08rasmt3ix2ympm05oxnab103l7sq9vzqbdhzm0szuwl89gxks2i15nplr9w424l07qmyp6te66vdmr2auoclznegpwkc450n8658nwtu37mn09lao9p03681x1zrtsa5a47vi049jaqjhtn9nvaiyl2ysahsmag4uyu8z04hl6mgqrt4gxfmynj025t4x0xv4bi6urx46iamzwun7qky9c1yv5w0kvsebhymv25yvyju2r2mxejp9epyfa3s6qf9zgsbjugffkttmbizrhnbgga53yzv0czyd85iydinb5xgv7o3bojgrrg46qfxsj6bmeq35ch1si6xvacfx6zani01kb9v2lar6icfp6y2n13k2msh9ws3zv4u0j6z0ss31tddcfognjmce3z5ne6p9cfz3pd4fvmhjlu5bo9lwumposvr7jl28hfqsoav8oen9rm0tu9g10ofh86gmakdnpanc5szzfac34jkqwvl8vaj2s3xzf8fal4qity5stbhcxhpyc4x2xj5i98mkw717tsfgdhqqvyb12gbmw5e5v63oyezgibu5q5v5kq758xi08aimrtdndre5ygg3op4jdvi7jr9xyt75m4ckikspyt3db6sbd1zf2ln6e4vvuqk81yunlpeiy306hcw3rffe7kgotzk3uv4bsnwwr1bcxkkqwwzq265r93ri1dfnapv4mm6eyr0f5fpgeucx4q1d20r7xxziveyrbp4yuhzmfm6n5syzbiht9zrzues4sliusp5b3f1pwsgcn61wyhvqk0lh6flzmwkfqgxbq27bxxpejpdxpizxxgkto3mugq4cyyebcerp2vmxs7cmsm626dr4kz6ojt5pnc176utfyan6v6k0d488onjxubseire8sp9bb62gcfb28w2y3q6tf6qzepa1ai8j9d97ghmaix4ufl7nup594ibaq1ok5mk5buvckkhczihr7vfcazo76m7sctg4osp2adlo7p85j9qbmd8l4scxi1sehnknupka3jognmu24w08sqkafl8ckza7pmp1ahe5q7nq0woo482yvrwlzxj6232uj2ee70jt8fwe72ac72xmrhrwuvagj9zglwjkkeo07kcap", packed);
        assertTrue(isPacked(packed));
        assertTrue(Arrays.equals(data, unpackBytes(packed)));
        assertTrue(Arrays.equals(data, unpackBytesIfPacked(packed)));
        assertTrue(Arrays.equals(data, unpackBytesIfPackedOrBase64(packed)));
    }

    @Test
    public void testPaxUtils1() {
        String value = pax("");
        assertEquals("pax01sv8jeh00000000xxap", value);
        assertTrue(isPacked(value));
        assertEquals("", unpack(value));
    }

    @Test
    public void testPaxUtils2() {
        String value = "fnwvow5v 45vg 45hg9oe %^#@$%^!& +)_+:}:>][;/[;\n\n\rHELLO!",
                packed = pax(value);
        assertEquals("paxg1f4hrn70gg00gj76unn7mmv35nm2g3k76mn2g3k6om73p6v2g255e2324i55ui120ibi9lv3q7tjq3u5bjriflr0q0a0t4o4c4c4f21xxap", packed);
        assertTrue(isPacked(packed));
        assertEquals(value, unpack(packed));
        assertEquals(value, unpackIfPacked(packed));
        assertEquals(value, unpackIfPackedOrBase64(packed));
    }

    @Test
    public void testPaxUtils3() {
        String value = "hi                                                     enable                                                                                          deflate                     plz",
                packed = pax(value);
        assertEquals("pax02t957gn0gggg0229csbc85407q4emi5u5ak9iali89g9ipap35882tdo29ciqggf61be4xap", packed);
        assertTrue(isPacked(packed));
        assertEquals(value, unpack(packed));
        assertEquals(value, unpackIfPacked(packed));
        assertEquals(value, unpackIfPackedOrBase64(packed));
    }

    @Test
    public void testPaxUtils4() throws IOException {
        byte[] data = toByteArray(getClass().getResourceAsStream("/org/cmdbuild/utils/encode/test/data.raw"));
        String packed = pax(data);
        assertEquals("paxghbrqt0000g0gkg0ii2ppckai4l06tslet6jqaja3hskdpasus5gh9acr8ikj3ku5602ivdiuuv1jl0aju8fgon4j60u7btm99qqmgp2qnoh8pikqflmlmsn9t7fq7002f9cebpdf4mcq21t993hdg614ca6cq1r644ncg6vb20jmcqnjp4sresv1rh37gg5t3plg48e1qs3kem51r9fgbvm6u29e4o9ho9king1o8147r11u678u4vma4hvsl62456v52k6apdkbng3ubdfu10qroomc2p0lhbegb7evd8bm45k7lpboj76fmv38gj61p48v99qg5fiech6a204n7h143sq7rqlf97ceigl6mmdk0u53k3ntddjsovm77l4akkvlflinoo7urb00dha53u65p2902jh2sr6m7eg4kr9nub1jh9mfsqfh2lj01gjk55q3a93760ve6c47r8i58k7vdmc6m59113v0gn99vm214lll9ubaa1o67g10rulg17sl2r5o2ubd979m99ikqdu45lf57753lqlpmnfjm8aterc4vcm275opvsfb80cv51dgftraeidtfd9d3nlvpdk87qljht258veifsictfef68hst6u8931l3lmg291h7nkfdhjlch3ck3qrjptjcdbssr7vg71r8kjoe9ol3ld450eld4488cj7n94vka6os3ud0rnn3a7se9osutedcloh1h7nnmgluhl7r9s26st176sgrkgp977apt07uefv58nohkunpacmf9llftleq3qo0747202s9u8d09ep2s5sc1luqn2sjgle2r1cku8qvk92npm3ctftfgp2riqj5tetlv5us8gqipuro55aul8li4i8ab9utbmenkedagkmpq384hrghotedk4dkiq57pt8cjqfo33qj7ismgp803lr3bth2sgc9bl5vmm7p8uhegkc74oga8dptm1gnbpsrghoklc7jjv4rfekm1gktkhc8um8ogvusttkm7hna6fpo3lpco83radodc7r9e1e63uc5ma2nji8j02ua5bq5421dbv75imjbh7nvrt5jh6ri5pp4vm7tr3853k6410npqtnir23luj76p5r25um573afgctsmkevnak25cvsrasar5eado38phar5pvjc6qperlt5j426vg512j1aaghlpppi7kru4gebogbadtau3q10mkah4pqr82f65lsp7rsunu16ursd1jpent5ic37mlpjmvepb715maok9uh6me5js0frpbo1njksvcjrm4qah0amgng1ou2jaoecdnso5pcdvsmrg448vjv357s3ippjbkvl4vnuoioq1v4j0uddvuki5h7rtgp37foqiee8nadgbeflgn5hjubn78v3606ldheki5rd83qpuhuseh3o5p2l5ld57b4903v6gll45rfvjarfh6gkefm8smbm3vtf4nugvdranb2f72mln4e8iok1lf84l3lf6heol9i1iatp4o6hv45pp5nsojtb7nmqe3umk2j8g1m17mk8rb48eufvquoi41q1nko0eqt4opgomdsbg0nige2tqdd8fdvrp7s0717p89vqvai2pfauf8e35bh8m52abvtvgliq86gpct0d8283xap", packed);
        assertTrue(isPacked(packed));
        assertTrue(Arrays.equals(data, unpackBytes(packed)));
        assertTrue(Arrays.equals(data, unpackBytesIfPacked(packed)));
        assertTrue(Arrays.equals(data, unpackBytesIfPackedOrBase64(packed)));
    }

    @Test
    @Ignore//slow
    public void testPackBigArray() throws IOException {
        for (int count : list(100, 1000, 10000, 100000, 1000000)) {
            byte[] data = new byte[count];
            new Random().nextBytes(data);
            logger.info("pack byte array of size = {}", data.length);
            Stopwatch stopwatch = Stopwatch.createStarted();
            String packed = lpack(data);
            logger.info("data size = {} packed size = {} elapsed = {}", data.length, packed.length(), toUserDuration(stopwatch.elapsed()));
            stopwatch.reset().start();
            byte[] unpacked = unpackBytes(packed);
            logger.info("unpack elapsed = {}", toUserDuration(stopwatch.elapsed()));
            assertEquals(data.length, unpacked.length);
            assertArrayEquals(data, unpacked);
        }
    }

    @Test
    public void testPaxBigArray() throws IOException {
        for (int count : list(100, 1000, 10000, 100000, 1000000)) {
            byte[] data = new byte[count];
            new Random().nextBytes(data);
            logger.info("pack byte array of size = {}", data.length);
            Stopwatch stopwatch = Stopwatch.createStarted();
            String packed = pax(data);
            logger.info("data size = {} packed size = {} elapsed = {}", data.length, packed.length(), toUserDuration(stopwatch.elapsed()));
            stopwatch.reset().start();
            byte[] unpacked = unpackBytes(packed);
            logger.info("unpack elapsed = {}", toUserDuration(stopwatch.elapsed()));
            assertEquals(data.length, unpacked.length);
            assertArrayEquals(data, unpacked);
        }
    }

    @Test
    public void testUnpackWithDirtyChars() {
        String value = "fnwvow5v 45vg 45hg9oe %^#@$%^!& +)_+:}:>][;/[;\n\n\rHELLO!";

        String packed = "packmd90jix757odes63ju5fyi800k68l9nhzyias5cpc8jbq41glzjqbitkfvvmb87o9011kpdix595kscbp6dqqg1vx5m60j6qf7kcap";
        assertTrue(isPacked(packed));
        assertEquals(value, unpack(packed));

        packed = "  packmd90j  ix757odes63ju5fyi800k68l9nhzyias5cpc8jbq41glzjqbitkfvvmb87o9011kpdix595kscbp6dqqg1vx5m60j6qf7kc ap ";
        assertTrue(isPacked(packed));
        assertEquals(value, unpack(packed));

        packed = "packm\n\rd90jix757odes63ju5fyi800k68l9nhzyias5cpc8jbq41glzjqbitkfvvmb87o9011kpdix59{}[]5kscbp6dqqg1vx5m60j6qf7kcap";
        assertTrue(isPacked(packed));
        assertEquals(value, unpack(packed));

        packed = "p,ackmd90jix757odes--+==63ju5fyi800k68l9nhzyias5cpc8jbq41glzjqbitkfvvmb8\\//7o9011kpdix595kscbp6dqqg1vx5m60j6qf7kca'p";
        assertTrue(isPacked(packed));
        assertEquals(value, unpack(packed));
    }
}
