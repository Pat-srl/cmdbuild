/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config;

import org.cmdbuild.auth.login.saml.SamlAuthenticatorConfiguration;
import static org.cmdbuild.auth.login.saml.SamlAuthenticatorConfiguration.SAML_LOGIN_MODULE_TYPE;
import org.cmdbuild.config.api.ConfigCategory;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.springframework.stereotype.Component;

@Component
@ConfigComponent(value = "org.cmdbuild.auth.module." + SAML_LOGIN_MODULE_TYPE, module = true)
public final class SamlModuleConfigurationImpl implements SamlAuthenticatorConfiguration {

    private final String code;

    @ConfigValue(key = "description", description = "", defaultValue = "SAML", category = ConfigCategory.CC_ENV)
    private String description;

    @ConfigValue(key = "handlerScript", description = "saml auth response handler/mapper script", defaultValue = "login = auth.getAttribute('urn:oid:0.9.2342.19200300.100.1.1')", category = ConfigCategory.CC_ENV)
    private String loginHandlerScript;

    @ConfigValue(key = "icon", description = "", defaultValue = "", category = ConfigCategory.CC_ENV)
    private String icon;

    @ConfigValue(key = "enabled", description = "", defaultValue = TRUE, category = ConfigCategory.CC_ENV)
    private Boolean enabled;

    @ConfigValue(key = "hidden", description = "", defaultValue = FALSE, category = ConfigCategory.CC_ENV)
    private Boolean hidden;

    @ConfigValue(key = "sp.id", description = "saml service provider entityId", defaultValue = "http://localhost:8080/cmdbuild", category = ConfigCategory.CC_ENV)
    private String samlServiceProviderEntityId;

    @ConfigValue(key = "sp.baseUrl", description = "saml service provider base url (es: http://localhost:8080/cmdbuild ); if not set use ui auto url", category = ConfigCategory.CC_ENV)
    private String cmdbuildBaseUrlForSaml;

    @ConfigValue(key = "sp.key", description = "saml service provider private key (Format PKCS#8 PEM, the one with BEGIN PRIVATE KEY and END PRIVATE KEY)", defaultValue = "pack22u79pnn63q1obiz1qkp7atyvo4o9enzyayazk9jjxbtwpxnhkkcsnsyj579qykl2dp9hkzq8ke6a4wghie6irq7m3boryya1uc6fymnigfz2zzxxuar860rdlgpwamrjvmugbjayv7zo0tr1vdsj2xjwwv0eynyv46pgyyhu1jhrgguxo7t8wey4qc8x4mbnx88t7tr60dn10osyprc8g9f6p7hwx43q4mavmim9pvsn5uj263y97zn15j7nkve4g9s9t0270oczdlfyhy59ctxs0la2h9e42919pzc8vq9xy6qdrkmey7ru98a1svdqobcacsv67b91xnes4pfavvzp4tpne4y1mo90obuf5ujhturiwkw58i35jkyv4tcwna7zmtpglx8j4kqalvt90dz961m66al37iqcgc1t05mpgx6wod09situezo4xhpo0muuryglw89k8tfcm0ymj16ouzjwivhaxhqe40q88cqprpqiyxhz4xnyauebswraguk6w1nv5e8dnf1012ni3q6kxajvjnyb42poredmmy59kvqahpnyq12pxlxiph3xgxnzev8r3so88f43mojpil447hfkptjm10ti2z7w9exhesbghdtovmnu5kriu8sspzlwiuwm74zq3xraqf3hw9j1q57n6fgjq7htv9p6vmjex55wl04me7alm278vphngt7e4ekclwljygw1dxmwd1cvfnhutv9s5jv80lifjabsyfonr772lvwgitfhedztryjg9crcr2vs342ijk2tt23gfiu0u0x0bv36jd4enah4gi4vt3jccbf0r7p62raem9h1wjr7lr2bkygiznvxa14lj0dsgus9xcuj54b1vztw28a3u87xdx0649i94p1ba0n1occ388t134hnzr125qut0ol4wyi1ojqq227cg60h2ncr6mlb96hpaqnkkj2z3hr4vck4gj7ly0bymmr86a31x33pmvoiww77pl7loz76a8d5dgl2vmk5h6t3d02gcy6i40449yzgjiw6yxugfv0f5t0bilq1u6kixt915aflkcap", category = ConfigCategory.CC_ENV)
    private String samlServiceProviderKey;

    @ConfigValue(key = "sp.cert", description = "saml service provider x509cert certificate (PEM format, the one with BEGIN CERTIFICATE and END CERTIFICATE)", defaultValue = "packdjicnlecaukw1x0enri46dngsfeukxz67w4wesmatbbbl809aueajj47kv00uj2k12exrnykcoa3lu9taxufmsh1x8wqceqi2i0rrsbz03cr4zigg2jxdzj78o51gq7txttysj0jrbafu97lot64ot5onbvkl1kjbh5ylih3gx03hw8nxhpexnm0qyhq5sd7m15dzv688sxflla0ef9yaxnlskl0uwqlo242mfj1mfvo20uujw1wj2dg0fj98wjn9pg22waijp7px6729aql023hcgwmw5917uzna2q9jey0lfejujgp9x2le90vx5vtonfl5kf47wvyf4sws7cdc28b55v8npbqtyud2laj9msi4whqf1o2609q58b85nhoufn95du4lh7xdssowy3qp450ieuw3y9qxehcbk7hgd4ax658gwlctqn09tdr5yawr01dfapti4tjownnb0z079k6al1ppoer2isaoxwjbqgybzzc6kchi80w2xnm8xay5t8cmlb5sxoewrr6oh8b2ihz1wzfxmz5tmdqi1r68pmmjmfev0e9z73xeb2vn1hyv4ktfp6wpzv7pzodae89w3mgle1gd2plqgxezo1ijzai08f548zk7ev3mwkxlj4ern28ylfzmvkwkwaot53u5h08zhbz99xc3lbroctcy8n2612fhk6x4lmvic7ayn9vok2na1lsdzt6yieir3sw52alhxoo7givs7ethlmmcdiv7sr2w4ckhbjp2fieweyafr2p1zfm2cy35otl3q67lt6ip7qup51i1qalquacjk73q2tuhsf2cpovxwnfedpgwlclvhz67mh7ktbpw3vjmkqb61exfs3r10hlwbt079adxxz5qfu168e2j27v8h681zy24mop1stcoisngvz8lnprtolisjmpitbg3s9bgkg50fitt747o6hggmpnzkcap", category = ConfigCategory.CC_ENV)
    private String samlServiceProviderCertificate;

    @ConfigValue(key = "idp.id", description = "saml identity provider entityId", defaultValue = "https://saml-idp-test:9080/idp/shibboleth", category = ConfigCategory.CC_ENV)
    private String samlIdpEntityId;

    @ConfigValue(key = "idp.cert", description = "saml identity provider x509cert certificate (PEM format, the one with BEGIN CERTIFICATE and END CERTIFICATE)", defaultValue = "pack18b4mriym73bjdo0ncxsenmauxpjm5pjyp9sont086aqo3v15ytehgv0ou82jjluabgdbz6edx3wltuof6dbmkv35lltprg07a0zimj2llyyid82r0bhp8jar8x3fzimi3g1fsitkp0tc87389njcxy86hrdbyzx0i8h4dw33j8ek770ngqn3c78h6dugxbvzvi35akz6n7f0b06daov13zwerneusl7j18jvtegtc7wqwnapk9iksrnsage4nwf2ennvq2kcb32ohy2lp5wxgpxfwvrhvkyayv1k5tlzhl6sajdjamshsww2k6ds75jao38ymns2gg8uvso1vy2xmf7dw0988el4x8hnpnbl2o37e9h3ypfvmmnt12x0bh0lcp4snck8podkz23qn6u19rnbfmb5fu562ibrz32pabqrq289wvx6whxlv7im2ajp5q8tztnrabs4cyke2tvmg1zbpbiqllbizqtqbb5u3g7ymbc1ct9d53nqsrlo94k9vsyah7l7e9znenrav9y33zjwsp32g7gwf3iu96t7pr3mtu5ztbu48hfrpin53jn3rmluwv2l1xpcbncbygqa4bkjxuumg9153gcr2s8ncc8ndher7wnmomg48zd9hp86r3z94uqp0zzypzy3cdiejis06br5tkqmbyi402yzvn52q2js0zq06r1qaza34vsb7yje7ridpvkf4xtnd5y05xp6eyzg6yomjkukpfcowvbaultypacfamh32tm9knlep0uja96962ynw3bwjpoj5sn976mac3n8x2gezhdvftct8r245z98e0eu9uuh1hla2hyulwfxvcnx5k470yksxb708g94pd4tc7jmosrtsy27glnwonzfwqgn03djs2r9hm04kofxpvjlcnagshjgx4owd9nu1cyygupa7pdynyc7ynvi5pw5hpj1eye7iozzfi4bj11gf35xtkmaqomg0326vu1burm24zwnv8xbjs3gswvg1f1179jhmh5yu9bcupjoi0u66qbsddarqsirrfuw2v2biovjd7osc06kc12204g418ow1eqed6asde9chycnrg2szf0zkmab5lyfigiyjll6ba3176y05l4ikz4lbwmbhs39kn9m68pr95jev74wczq8x9njd0x7f43qyqpxkz29231l129fyyq5dc721ja4sb88crfqxywtp25gilfbi7yk3j5kntbqhybt83qskhe84jmzxsgrggusl8jtd5naqpeqbwfdk3id6nvgohziate2klabud5jo38f8fbtuidwrfezjs6emcn53jlqo59fqqawe9whr6h4sx5u6c1hjjkfb2n7ehuy1krag8hfcr4z1amab0mw33oj3u1b5soxs8jxdd7b7mst9pzsdo4ely3irn78gj82vt2cgdnxgmj9iblcw31sgvirwd6pff3kujxzwqm7p10xk33cj8q4958rw4dn0nii2qxux2ttmqpbxmiywtmdj88lesy4lm6nx4t34slsxikdbfjzi0wv5gjwwc8uxgg1lqdb5613on4z0zb2cc3fs48mc22m97q65c5q4m2mamnyoo58oormhn4d2ovr3e1ygddb8o7xgcbk1h3gzh4ctqkth079uurcwnciwl6zaij9wuja2v6vupwq7adms57t5z6s1hlpvuby9c1x46xsnkcap", category = ConfigCategory.CC_ENV)
    private String samlIdpCertificate;

    @ConfigValue(key = "idp.login", description = "saml identity provider login url", defaultValue = "http://saml-idp-test:9080/idp/profile/SAML2/Redirect/SSO", category = ConfigCategory.CC_ENV)
    private String samlIdpLoginUrl;

    @ConfigValue(key = "idp.logout", description = "saml identity provider logout url", defaultValue = "http://saml-idp-test:9080/idp/logout_TODO", category = ConfigCategory.CC_ENV)
    private String samlIdpLogoutUrl;

    @ConfigValue(key = "requireSignedMessages", description = "require signed messages", defaultValue = ConfigValue.TRUE, category = ConfigCategory.CC_ENV)
    private Boolean samlRequireSignedMessages;

    @ConfigValue(key = "requireSignedAssertions", description = "require signed assertions", defaultValue = ConfigValue.TRUE, category = ConfigCategory.CC_ENV)
    private Boolean samlRequireSignedAssertions;

    @ConfigValue(key = "signatureAlgorithm", description = "signature algorithm", defaultValue = "http://www.w3.org/2000/09/xmldsig#rsa-sha1", category = ConfigCategory.CC_ENV)
    private String samlSignatureAlgorithm;

    @ConfigValue(key = "strict", description = "strict saml validation", defaultValue = TRUE, category = ConfigCategory.CC_ENV)
    private Boolean samlValidationStrict;

    @ConfigValue(key = "xmlValidation", description = "saml xml validation", defaultValue = FALSE, category = ConfigCategory.CC_ENV)
    private Boolean samlXmlValidation;

    @ConfigValue(key = "logout.enabled", description = "enable slo (single log out)", defaultValue = ConfigValue.FALSE, category = ConfigCategory.CC_ENV)
    private Boolean samlLogoutEnabled;

    public SamlModuleConfigurationImpl() {
        this("___DUMMY___");
    }

    public SamlModuleConfigurationImpl(String code) {
        this.code = checkNotBlank(code);
    }

    @Override
    public boolean isSamlValidationStrict() {
        return samlValidationStrict;
    }

    @Override
    public boolean isSamlXmlValidation() {
        return samlXmlValidation;
    }

    @Override
    public String getType() {
        return SAML_LOGIN_MODULE_TYPE;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getLoginHandlerScript() {
        return loginHandlerScript;
    }

    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    @Override
    public String getSamlServiceProviderEntityId() {
        return samlServiceProviderEntityId;
    }

    @Override
    public String getCmdbuildBaseUrlForSaml() {
        return cmdbuildBaseUrlForSaml;
    }

    @Override
    public String getSamlServiceProviderKey() {
        return samlServiceProviderKey;
    }

    @Override
    public String getSamlServiceProviderCertificate() {
        return samlServiceProviderCertificate;
    }

    @Override
    public String getSamlIdpEntityId() {
        return samlIdpEntityId;
    }

    @Override
    public String getSamlIdpCertificate() {
        return samlIdpCertificate;
    }

    @Override
    public String getSamlSignatureAlgorithm() {
        return samlSignatureAlgorithm;
    }

    @Override
    public String getSamlIdpLoginUrl() {
        return samlIdpLoginUrl;
    }

    @Override
    public String getSamlIdpLogoutUrl() {
        return samlIdpLogoutUrl;
    }

    @Override
    public boolean getSamlRequireSignedMessages() {
        return samlRequireSignedMessages;
    }

    @Override
    public boolean getSamlRequireSignedAssertions() {
        return samlRequireSignedAssertions;
    }

    @Override
    public boolean isSamlLogoutEnabled() {
        return samlLogoutEnabled;
    }

}
