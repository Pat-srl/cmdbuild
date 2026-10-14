/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.crypto.test;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import static org.cmdbuild.utils.crypto.CmRsaUtils.createChallengeResponse;
import static org.cmdbuild.utils.crypto.CmRsaUtils.createToken;
import static org.cmdbuild.utils.crypto.CmRsaUtils.parsePrivateKey;
import static org.cmdbuild.utils.crypto.CmRsaUtils.parsePublicKey;
import static org.cmdbuild.utils.crypto.CmRsaUtils.verifySignedChallenge;
import static org.cmdbuild.utils.crypto.CmRsaUtils.verifyToken;
import static org.cmdbuild.utils.encode.CmEncodeUtils.encodeBytes;
import static org.cmdbuild.utils.encode.CmEncodeUtils.encodeString;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RsaTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final static String RSA_PRIVATE_KEY_DATA = """
                                                       -----BEGIN RSA PRIVATE KEY-----
                                                       MIIEpAIBAAKCAQEAxvOu7j/mQWfpxTh4D0dWunY4vrgH4+Qk/k/N6Qpj5IiXl2T3
                                                       uDH075q3fK9FfPp7IgUGaUwB9KOMMuBvw4eJhl47q+bIdS2NkDqOAm8UEEk07wgg
                                                       EZ15A2QDV8SzOc0AwnFDeRcuN6ftP1pUIaBJyPX7BcyFIRFCPzWkeEdDm6Limt71
                                                       /pLj43QCyQa1oBm7uQqLElcBLvYt1dXlO9ucVi+VuxP6LZoE+Xn7AyToch2jJxPZ
                                                       Or3ruYMNntVeZPsfzKrYBt5ehH1iDZ6L0+FBqIzzULGoxOgS2KcKc1wbuKR9npVM
                                                       56d9MT1GyyV4B5hhIoUxQCiqIeMstksuvuOo3wIDAQABAoIBAQCSCtdidO2lHY5x
                                                       A56+OHPltFq7RYQlOZgeRp60brgdTldY/vkI8UMHj98ZW9/6ejRhKKcoLG9TEasc
                                                       vbvNIHVHeWz8JxEHU0UTlqggCUIFIngE70X7KElov8Xka7PUvjPMiDArd2Sp3k89
                                                       riUL+gmvx0FZZGRlfbHPjKUK1eU/HOi8PRz2wmkMXZqG13I9rHjf8GGjxieOsLxz
                                                       qAdSiD+ZvUkCBeT+kg+CQQNjAgCqwuPVQbIpwglME5R8twWN60PdA6n6c1QCXpSM
                                                       FWDAnpNMiEf9gF7yYMen4V+bOv8R29mtv2phqJUDA9SK0zaXCzcjU/JIYwcSpemj
                                                       ufvciIcBAoGBAP82/DBHjb9G9Aifhj9QP0QqdLWrQ+qyGyx+iInKv//yNvK+Rspq
                                                       PKmvzKds64jSq+HMbr9FAoGElUFX4+GoDwg//XT9wYsbhSahbxCIryGUWChN3ghq
                                                       scOdaUFLnI0f4F1lyjyery7padELEPkaIw0av0j4EQ2KHpPjKNRWMoA/AoGBAMeQ
                                                       YkQF/g5U/O26owCuwC/t5RImhFR3Z6SIzz8TdEZSqVVyxXGjnKCMiCdFOcImKYqQ
                                                       iBI3A3MoHELV4K5T+wvR24SMhZ9LU5hRwo8/tIqo7gmUrC6cEh0JbY2S+HDVzBbO
                                                       IES/eLTmWwIMwGO2d+nYVSvMGPoR0D0TZZ4Kaq9hAoGAMb6f7TElOdE0o7GkUxbr
                                                       HfhAg2B8fnR0w3luV51DSzoE58+asL2AUXrIoyGhzytxATP9qh5jXKdhCDl/W26M
                                                       b4k5d+I2JU0Z6OuyYECkEiF+BW0YB//z4jL+XxHD0+YQr2O1xZcQgk5Qp8RGbKdh
                                                       iZ5bBngDquXvC9hNDwY2FbUCgYBviM6i3gaD1BQYebsoYoKfRwJEBqrm2mVem+fY
                                                       hPLzarehPigDv0GGqYRfDl2dmN6WxviF1aFj9wL0h7yvMdiZXoylpDP6N4tdEjT/
                                                       AsfQx2FikWk6E5g8CkzkV0PrLFKIXGPEiI7Z6/TpTF8qW4zhcTsI42UCPnp36CNf
                                                       FMtTgQKBgQDTyGM7CiH/5R0DXRBnfZDbWUXObCBbyZaXAsQcekc5qLMLb/dgPLSO
                                                       YQC1E4+6EgQWPPccwHsVi7P3vL08s8iqrOIg1OGDCaY83yDfyw9bC4Y/GH7mTmfC
                                                       fwOygNv2fh4tgs6gBKdf2HBOpXGY9RaWT8ZyEyX6VSgLykSCQBxSXg==
                                                       -----END RSA PRIVATE KEY-----""",
            RSA_PUBLIC_KEY_DATA_1 = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDG867uP+ZBZ+nFOHgPR1a6dji+uAfj5CT+T83pCmPkiJeXZPe4MfTvmrd8r0V8+nsiBQZpTAH0o4wy4G/Dh4mGXjur5sh1LY2QOo4CbxQQSTTvCCARnXkDZANXxLM5zQDCcUN5Fy43p+0/WlQhoEnI9fsFzIUhEUI/NaR4R0ObouKa3vX+kuPjdALJBrWgGbu5CosSVwEu9i3V1eU725xWL5W7E/otmgT5efsDJOhyHaMnE9k6veu5gw2e1V5k+x/MqtgG3l6EfWINnovT4UGojPNQsajE6BLYpwpzXBu4pH2elUznp30xPUbLJXgHmGEihTFAKKoh4yy2Sy6+46jf davide@phil",
            RSA_PUBLIC_KEY_DATA_2 = "AAAAB3NzaC1yc2EAAAADAQABAAABAQDG867uP+ZBZ+nFOHgPR1a6dji+uAfj5CT+T83pCmPkiJeXZPe4MfTvmrd8r0V8+nsiBQZpTAH0o4wy4G/Dh4mGXjur5sh1LY2QOo4CbxQQSTTvCCARnXkDZANXxLM5zQDCcUN5Fy43p+0/WlQhoEnI9fsFzIUhEUI/NaR4R0ObouKa3vX+kuPjdALJBrWgGbu5CosSVwEu9i3V1eU725xWL5W7E/otmgT5efsDJOhyHaMnE9k6veu5gw2e1V5k+x/MqtgG3l6EfWINnovT4UGojPNQsajE6BLYpwpzXBu4pH2elUznp30xPUbLJXgHmGEihTFAKKoh4yy2Sy6+46jf",
            RSA_INVALID_PUBLIC_KEY_DATA = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAACAQC6V9nUuUdixTUtKDBcaATknmM3d2JI+j2jaMiirgdrnRjAAfMnAD+dbnokmKfFOKrUhgVhMn/YGyXh2IyEsxb+mFj5S9qqKCqfRNp1fomzP9r5HoGnHut3ZwHQPz0ZVqkbGGulO4vJzUxhdNp7usRcbZyuuoRkvlLW5B1/IzTW2QBRdtevd3ySOzkR6yDBsN0sB+U4rW0wuBYxq2Dvs9F01QnjZIsyB32Z9sHGOf49Qj2SVUS4/Sx86rd1V6Yly5Gn4jlum6dzMHSXa66SX7QtVEOR7aKbHSV1wmZSW9FYJyR3enDJg0YtBwBbprFacoE/KeTyOQBBe4nOLhFjkMbDgUasdCGRtDDP+00os6DZB8YRNg+lURkSdz01vSQtMH5yLQOSSIjkCTSL2IemCM0KpE2J2K/gn4QdctYitcbMJeWnoYG1SRo8sH1mdHSiuUK7GB+DrXorAYRiWqHMP98t4NWaiFFgPwRyihXkoHIvcLCDYzFQm8WqT2DWvQVkRaCcpWgd39V7cqHLWrUY53/vbSZEC+ndlpQI64x+DfxLQ3b9S2Hy/YZON00aEE/VxAVo0iMQN4GcWrrkoTwi3f4XIfCcZb8ySTXkrliQHIymU3NimSW+696/BtLKT/nVsUDPLsfWlaGALBn84N6Hvt9Oh93QXbpqyJPhRo2U9uRx+w== d.jensen@tecnoteca.com";

    @Test
    public void testRsaPrivateKeyLoading() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        PrivateKey privateKey = parsePrivateKey(RSA_PRIVATE_KEY_DATA);
        logger.debug("private key = {}", privateKey);
    }

    @Test
    public void testRsaPublicKeyLoading1() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException, IOException {
        PublicKey publicKey = parsePublicKey(RSA_PUBLIC_KEY_DATA_1);
        logger.debug("public key = {}", publicKey);
    }

    @Test
    public void testRsaPublicKeyLoading2() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException, IOException {
        PublicKey publicKey = parsePublicKey(RSA_PUBLIC_KEY_DATA_2);
        logger.debug("public key = {}", publicKey);
    }

    @Test
    public void testRsaPublicKeyVerification1() {
        PublicKey publicKey = parsePublicKey(RSA_PUBLIC_KEY_DATA_1);
        PrivateKey privateKey = parsePrivateKey(RSA_PRIVATE_KEY_DATA);

        String challenge = encodeBytes("asfsevc5et4wg14t2v5g346ygve".getBytes());
        logger.debug("challenge = {}", challenge);
        String response = createChallengeResponse(challenge, privateKey);
        logger.debug("response = {}", response);
        assertTrue(verifySignedChallenge(challenge, response, publicKey));
    }

    @Test
    public void testRsaPublicKeyVerification2() {
        PublicKey publicKey = parsePublicKey(RSA_PUBLIC_KEY_DATA_1);
        PrivateKey privateKey = parsePrivateKey(RSA_PRIVATE_KEY_DATA);

        String challenge = randomId();
        logger.debug("challenge = {}", challenge);
        String response = createChallengeResponse(challenge, privateKey);
        logger.debug("response = {}", response);
        assertTrue(verifySignedChallenge(challenge, response, publicKey));
    }

    @Test
    public void testRsaPublicKeyVerification3() {
        PublicKey publicKey = parsePublicKey(RSA_INVALID_PUBLIC_KEY_DATA);
        PrivateKey privateKey = parsePrivateKey(RSA_PRIVATE_KEY_DATA);

        String challenge = randomId();
        logger.debug("challenge = {}", challenge);
        String response = createChallengeResponse(challenge, privateKey);
        logger.debug("response = {}", response);
        assertFalse(verifySignedChallenge(challenge, response, publicKey));
    }

    @Test
    public void testTokenVerification1() {
        PublicKey publicKey = parsePublicKey(RSA_PUBLIC_KEY_DATA_1);
        PrivateKey privateKey = parsePrivateKey(RSA_PRIVATE_KEY_DATA);

        String token = createToken(privateKey);
        logger.debug("token = {}", token);
        assertTrue(verifyToken(token, publicKey));
    }

    @Test
    public void testTokenVerificationBreak2() {
        PublicKey publicKey = parsePublicKey(RSA_PUBLIC_KEY_DATA_1);

        String token = encodeString("token:v1:ebct2rozsl5flspwzhlrgdut:1574347415:07v2twzmpvwwvslxyd017vtz1pgjae6d663xu8qqju5gite3kglzu61sh0s57z4ma620gwoiccotyve1s8wrwre2upr4comcllqj6uyit1636dpb13ohwh3d5mrm0ibknrd1jqtmifc4d8iexhe8748b44mgx5pss57hod5ka459pxru6hqy0wk2s2gbia51hehz2sfuodu549m4j27v3hkkinfyke752lt27w31js8675b8x6b553mfdt78kuu0nbuad6wv3qye836jbo90nozfr4s2v0oxjhud0kzyvy8wsybt3e5hpf811w5u0bomcupa0sorgiz73m78vwqkc5hvya3qk79uvdlr40cs3jhtvh7mznowmv42lwtb70t7gnnw0l9bx3mh");
        assertEquals("3xhaj79wbvw98hqrtk4zcv775tp9st00mag6r31p6sz20emgepm0km2e20m0dn2g4ptf038ojclwk67bcb69djj4wf1rtba55ciom7iw7dvxdo3zizl4qaznqb99f1h8zfj3xs3o8cj3g0p7kx3krmb4q8gorxepy53c5efs8e35od78fppne4b8dz1p2lnpc38c5v2y48x68kfy34d8zafasvdhpdb08h0mey3fs5spchjpa4t351ydtq03nyiyxau0f9zbiss8j7in7of2m7adanmtk233kfne9j34wm62jd58047jdqrw1vwo8g6pzdvyys6bvssiuk31o19g9whsjbbzbxgp0er5j0qv84g08jwbpo9scxpi8qir1wwdwihr5vu8nr7d0znqugqgjf4rt0cgg5ilwacwqzeeckhjr4oci6rwm0hzwrjop9utqxm5c3i5aucouqlbn7etvavp0u7tx117ay1fntxrz58v8lvp15zudamkenyecxrc6vx66m16q67oxyivw29q5wizt9aodfa2dd5qymtbmrqy0osy58vimebuds6hk8bm9vb1vwnsvcvdqqrep36eznk9kq9kbeyafcuvlb4s7vvf33ijt44ducq7x887rrb2al1vgfme5pv7lct2615mf8p08mfk534tutv5yuyuqc1", token);

        assertTrue(verifyToken(token, publicKey, true));
    }

    @Test
    public void testTokenVerification2() {
        PublicKey publicKey = parsePublicKey(RSA_PUBLIC_KEY_DATA_2);
        PrivateKey privateKey = parsePrivateKey(RSA_PRIVATE_KEY_DATA);

        String token = createToken(privateKey);
        logger.debug("token = {}", token);
        assertTrue(verifyToken(token, publicKey));
    }

    @Test(expected = Exception.class)
    public void testTokenVerificationFailWithDifferentKeySize() {
        PublicKey publicKey = parsePublicKey(RSA_INVALID_PUBLIC_KEY_DATA);
        PrivateKey privateKey = parsePrivateKey(RSA_PRIVATE_KEY_DATA);

        String token = createToken(privateKey);
        logger.debug("token = {}", token);
        assertFalse(verifyToken(token, publicKey));
        verifyToken(token, publicKey);
        fail();
    }

    @Test
    public void rsaSignatureTest() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, DecoderException {
        PublicKey publicKey = parsePublicKey(RSA_PUBLIC_KEY_DATA_1);
        PrivateKey privateKey = parsePrivateKey(RSA_PRIVATE_KEY_DATA);

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update("test".getBytes());
        String sig = Hex.encodeHexString(signature.sign());

        signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update("test".getBytes());
        assertTrue(signature.verify(Hex.decodeHex(sig)));

    }

}
