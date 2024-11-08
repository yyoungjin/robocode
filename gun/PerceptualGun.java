package systemprogramming.gun;

import java.util.ArrayList;
import java.util.List;

import robocode.Rules;
import systemprogramming.utils.DiaUtils;
import systemprogramming.utils.KnnView;
import systemprogramming.utils.Wave;
import systemprogramming.utils.genetic.DnaSequence;
import systemprogramming.utils.genetic.DnaSequence.Gene;
import systemprogramming.utils.genetic.DnaString;
import systemprogramming.ags.utils.dataStructures.trees.thirdGenKD.DistanceFunction;
import systemprogramming.ags.utils.dataStructures.trees.thirdGenKD.KdTree;
import systemprogramming.ags.utils.dataStructures.trees.thirdGenKD.SquareEuclideanDistanceFunction;

/**
 * Copyright (c) 2009-2012 - systemprogramming
 *
 * This software is provided 'as-is', without any express or implied
 * warranty. In no event will the authors be held liable for any damages
 * arising from the use of this software.
 *
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 *
 *    1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software.
 *
 *    2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 *
 *    3. This notice may not be removed or altered from any source
 *    distribution.
 */

public class PerceptualGun<T> extends DuelGun<T> {
  private static final int NUM_POINTS = 1000;
  private static final int WEIGHT_BITS = 7;
  private static final int COORDINATE_BITS = 7;
  private static final int GUESSFACTOR_BITS = 7;
  private static final int NUM_ATTR = 7;

  private static final String SKNN_HEX_STRING =
      "0xD10FA3F5D2E2849DC483EEA6AE53F71275C7CD9BAC3D4CCD79626D0D699C383D962BE9D930EEB61575C8F8617E6CE8DB42E303A7671F932E5250FC090A3DEE5199A217A3D7AF576891E95F6A08B07B94D0C1A12DCAB2741C94BDEDA05B9FB60D2E81ED633B429EC752F566CF698E219D61DBFE646FE6E73A7A0FCBBF504E923F8F0686924D7549E0307C921A0023315CAE5F456AC5DC653B69E8E45DAC55A34E96B4A85EF91F6A958F9BB39859894A86E814D485B08F508C15C8FF623D20D746B8648FAC612A7E436B2FE4435DB8B2DE0CDFB505D69321616B9A28BEB569A7EA92A6EE401CC792BC9893691C865CC1A7230576282C27407D3A80F5B3DA01B6BCA380BCD59C4E7BC8CD9E0FBBED2E331CB2F85800F3837B14A858A2CEA09EB2A94456F7455A3024300E36CAAEE50ADFAA8A5C5EF4A9E89BFD25578991B3DF470CA1EACE3C6042935AC42DE9A6C7ECDB9331111BBF0DBAEC69FB27101E1A19CDFC75842A8D56068AA9441D29510342FD202181AF7D66E8A951D606AF5BA076B8B7EFE940ACCD281379FB11B24B0541C9E4E2FEE196BB6DED84429F0780E3F9DE57EF8F0E1A0AAA086FDF926AA10A256B2BB36BF16CA31BB957E33BDFEFD30011A81286B0683EB17519621ABD3F1229A051ADF5CCB59EB12B8CB102DB2E6E8FB9DFFD2053A0EC91BFB7E6B27AC30C157ABB32EC7FE011C1C41F63078928273466593586F8C74DB1DEFC84801259C2465726734C904FF3E70212B83001BA7C27E700B71DBB441F68D545D8945DDEFEA69A542E2ADD000D5561EC8BE72821E1FB6271B2C7DFCBA1ADBCEA445D9F0028344D262020C4ACE0350EC262A9FD2B82EB53F9B7A77E333802669917689E92FFB6BF4A23006886794F5B939278359B7FFC0BD4A205ABFED4349220FFA0A8A2E28852FE8F3CE72975032434A07A9F119ABAB83FC0A78A4586719BD37A66D3C69132A546DE4648233E6C8524506E60AFD13F919C944A728A72324B670EA01AAE4DCBCD780DDF26E0CA388B3C1537A4161E4093456B459AC999F170A8F0B423DF1921A545270CA6966D095FFE7A613729915B038CE0066033E8010CC2FF611DD8ED6EAA9960F26A482E756BA7C7E3819815FDB9250997DED15907922530BED3AF697DFF36BFA950B0774C4038DD5C6BE1B64DFFF4536AB3CA2E81ACA73455914F802EBD0270D4E40B8B56E907CA134101E12214DDFA87ABB493A2ACB184DC8BE163BF6D021A3C6E72A80BBB90E346DEEE517DBC825C93986ED012D9E8F430329A7B803F8890543BECFE6E4F79804079C4EA4AAADFC0B6453952599CD70E86C1A000E38648D6F970EC2B86446727B93A0394B2114A35BE9876939CB4BD4A74C6D96683AA973A682C6C9DD6248EC3B27370112ABEC2D03975350B539FCEA2907B88B1E94043C5476CB88F9F77DFDD176B5982EEA511F1FEFB74F39AE4B111EFB7D1A4D7FED40E70E0829573D9CEE759AA593763D1D95B1BE9B1405AC89B3FA9A0ED671DF30FADC9D914180EF3577910921BD65F7C5AEC7692FDA99FA6469B64B30E9C5249F65550EAA2B5C6D2EE1AD286DA99C1E10E46ADA00D680D5219FC0C2984A2439A665EC953A86F1E569DCFF99EB6FB30073AC034208AB176C235AF0FD97C7399D311CEB14C3A824E86A444C5B59053D9B899E90BF52FCA73E3775D34FF609860369B5B9CC0FBABF408F08FB488E9FFAF660223E1718B89B6384A381817E26F24FEDA8D11703810E12C393CF4E817E92D27BFA530189F880B0627B3532821E1476F99398E088482E347640CBD4F70FD430D5004FF7B58367D911AC850CB1D3A44DE6D10EEF28BB99F1A26A6BB04D672723A1D20EA9E3CB9115C070116E39846BB9193B23182A20C0DB1AF74EC0F991265F28297B20221A29E58523191BAF37994303819A243B5A109724BB6DC0C4FB74E3EDF8DBCDB68B7F3B50435D9056164158A3C50C00A917CC27A40EAB3A6B26F6808E06787608DEC0A15ADCB9DDAECD10129215B629947C38B7D72DB0671728BA3314121FF99AD8551D87B950F932D594B03F0941F691145D72414BAD0A9F663AF3147B5F702FB881F9F0F4194E2B8B1473A0AB04407530E9F791624B5D8C2810AFF5D8FA91A0B4897A3A32F260FD31B5DBC46E40A52701FCCD350F3E32D716B0E8BE5D22237BC02D4DD03C486D9495FF32121BD169A7455A55283C6C4F024AF1EBFBCAFAFE734F0A4262A333E024CE492456C7BA6BEE18D34E668412C1212DA2E1426E481BC5DF158576A99A16E873058534C2BF063B9E1B529E0DC8830AD9025413A5F46A7B206B74C3E0095881D1293AEABB33DE242B169EED32BF0FA4174FE8FFBB5CF8B513D6B3E6BAAA6F290DAA7E8080C7ED9DA4A1AFA1EC2AAA16928B79B391034D4E4A479F61268E86BFD9F0D4001801DB77EA5430E6820D27219A2A8FCFC309BF13D3467DECB4A6E138380EFEDAD70A6DDDE0255398589AC275DF54B6D41BC24AAD26F36B2B249FDD266FF97C8E9B37AF8007BA88B77609F9A1F81166A2CFDA5EFF151EC103B3E14657003224BF941E5A4F879646E882D1FB8BFD8D07C9651477EA095BF6C0F8C82AE265DCF1E7FC7E79BBE03CFBD14C7250FD0E826088C8FD24906DE9BFCE44D3D90E60CA220B4688A95CCC26BE1E14480481EC63AEAB09B5ADD8AE7598AC31EBAEE4E3EB740EB2D90F11E5848FD5715F616203BD4A6734AE15226ED7E4B62969171BFC4E121723F1914FB91810704F39EF7591EBF887859A1865CD96B901E7D05477004A38D1F803B46457B90339F779C2BA55ACABD1996D6657B54881E7906305409707F8F7F303E8100AE419F7906485B4DADAF1EB74289DAFD05309A59C52E3B73E984113F8AC26FB4D3D8DA3F3809C273E2299B0915043E1B46A8623240A0C30266A75F377ADFA2E7CB0EAC8A4D7B63B90076E49BF7A5275947A62A42BBDC014A067A489AE58C589365389F8C54A60440E09668D3A0990F7018446571CF0F5642278FD50379EB43EC4920475A7802A238C3867F59AB1F6E483B22CD6BDF7B27888A692930437536A148459CF47EE9D8719FC10A1A0F155B0219DF52DC2888118B3C9F4EFF090388B909ADC214382E852DB24CB0B3A3E16DF5D8DCEBFF48B85914F7A72707C122329061C0B23E08D28420D2EA9CABEE858F4C16C187D2588F01C800B44187A277D8A5C0004EB10EC64F93D6F44D58EA18A75C9375BF2955612DB62E83E4A7B06C474BA47CDDD8E5ACDF71713CDAB7999ED27DBFD6D2A8CA5F43BBC8ADEE99D825B8EFC2A29733C66FD4457DDD92DBEEC994D7E9262002D507B2223265E81948CB87174B140DE54DD4BC921450FF8146BCA7F5A29A4BA0C56A425CA3175C2258251C72B0099CC059E53F4C64CE7CB2AFB92341977C46D5C81714A97E65B127D5AB81ECE73E62C50A1CEAC5719009EF231E8916D3116D3C5A45F9ED12AC7EA08D045348F630FBA278C70A579995F60D013A0ACC35CBCF634918A6626C9463FE41FEC62DA7B28BF941EFF117FD623516CFEF73E78DF9E5FB23D729C5098C415E9E07A086EDCE3F243401519BF9B8A58D38E5EEA7ADB45F61D990649E457FF2C0267D01ED846D89B805FFA7B6C26ED93844E1FD1B35E9065670F8BC38669BB5E558986840F0F64EFEAC48442C7DA6202F3D7C3C1E837DEE4D2F162CCE3726BB686BC0F2C263F11D579AFDA90CD2EAC8F35B41A0D24C0B0B390F7BF0A987CF8C2E1D41D46C39A3AD42E31658FEE78A178B3C1A5E1E2E2B731749540A03CA8FEFB66FD2AD05E290ECEA8C02C03D5E31C1BEAD376E3279AACB37624E9419565B927D30DA10B6B12D7504B426F2FE0A2E5CC1195E9E8EE2D7D2766EC3085BD49842D1C8C248CCF281F7B8A3DC4E649A66312812EA5A75728D20B99C7AFC9734831347AC30255D74747AEEE783BAE93C72192FEE234FD4E813C5649F4204A6F9B61B2A11B180C92A871985B3B7DC38662F27EB415D511354577E8820F495B14F0E41F35A645DA9AD585FF95ECD6A86BE02F11F75D1FA3874165326E35D4557BEE3D668ED5A718632BDEF2BC55783A2D1E1E4908A6B4090FF759E8F9B87A896D8E6CC9729B59E92EB4AF1A643D8DF14F156887C2B017356AFF2B476985B588386FEF447C91E6675222F62E1B1688AACACD1A4B7ABCB110741B03FF179C8A34932E7DFA710EC388C8AE982465686CA05FB3DF3D4A6A33B733DB7A447AC4103C13AE7A547AD7213E64E9E597DB9263878AB10C3214E730DE17777D7F7E68FA6078AFBD951054D91FFD814EDFB02CFBA6D8D7D7565DC185126CE9A86E36ED5C2FC5893E3D625DC3B40E0FB8BDE5F334D4E1CA39C35BC47D9C2F0B3AACA180E7EE82CF7A87F9BC564F2D0B8CAC8C052A6B0FF7A180B4E189BB26D2F8C38399D41DA52182155BF21B27C221ADC945012393760391092FB98C9D5D00719D491F18FFF247959B343A8D2F2B300F2E52D97282E993893A4A10C8446BFA4315AE0CEED7427C1F0BB743DF9EFAF9C7C50922903F718311AE6D30F5B27B7B907ACF1846F614FB185DC40F90C57BC3DB85D7A86D2605BB01FDBDB49A09B47AC856A6BD10D91FB22FA66D36935D3D44B70F639003C53D74E6D596A832A56CC6DBB529E902687FBE64C19B2A4C171D57730161C13E8796B4F4826B010B9F5C8B49CE3EB0760924B5332328E0128136636AB0A668C418BB80544D0AFAD56E487F61DF23B20A76333DE5CA17DC47095162E362260E8276702D9445E6E063E7096DF7DA52D7701A13C42943CEA613B317A157E6DAE046485585C99B60EC0519EBFAC54A3B9E736772114A27F0718A68522F7E3C9AABC0A1DA9CB6F5DBA04D8387B18D5F39B42317EF31804A7B92D1C980F687042A265868344C9D3E2E059A4392FF5BACBE467D1D889E677B1A52D8274961809492139638B4B5A51C936AA3FAE6AE0466BEFC218F65DF1FC15097D7BF4F32C9DCADAF6DFACC49E0CF2794A879B1AB4AFCC4BEEF37603985C2394E5B381BF5E2280EBC42FFAA2EAFC065A99DB0538413EC876A50BDFA12AA8FBB024CE0AD330015DEF875C04128B1FE046631F029438671B71C880B03BE9B5F9FBF573216AE1B60042D5C9B4FFBAA5A65383A9AF54BCE840D42ABC274D350B5CFB9EE9C189FB9F4A5163E65A7C276F0DDC9056CE36AEBDFD3FC60C5233B9039FB3A74A818335466F0FDCBE109C89847AACE4BF03F12B74F69A0A2C1451BE1EDDC8CD86F54D6A3BC0F2FC7F7673FC6EA1461CF4ADAFD532663BDD845F1F1B92CEEE7A00071F0E68A6A736E5BD14E42390429CDC2527638A3E7938848EDB6E2BA4D7C299C13F51938D06BC31AC2CFD7BC992BE3866C24F537DC67C497F1185922FF71489214039CAB012C5CDF86B255E5FFEEB47665BCE3F4975EF19395F9FB41DB08FC4AE7DFEFBC326FA3ECA1C8BF9DDF6A0E883F05C68E3204A4233741AC14C3EDFE085A0D326E610116F8A6710FE6353ACFE0A3B3F3D1E134F9C97BFA27681EB501F2F601A507FF125619984BCDB1E375A4EA3224E8E5749278BFAB390D5429B3444E88622168B62019BE960D3E1A9F46578D154DCDC3122A47D1214EF18936A82567694D2F21EC21B52D4F3139EC9094957D51DDA5FC5F5CC852DF47773B6D3BAEDF651EF568B46BCBDBBF5D3EFE25741E536861E94A6DF7CF371B120B3F812BE78B9FACC5E244070A70CE6FCEE2790EEC58A18A24A5244C0021C78A2E33C7A04CF161045AF11B2F3567D17768692050CC9DAC03AB13297EBBD7304E437F5E79332C0E37784D4A7B64B52021918CCE6C945B2764B7BEE68509D57E411E7C8897E5C60ABAE5976352FB78AB378938DC12A11CC0155F1A20987F601AD196E1F2EADA941BCCF087E33A5B89C1FC2D5CF03C51F7518653E93549DDA22FC56EA0560BFCAF2B2458C3942D1B420FC2817FCD4C63E2CC7728D7ED8CD5C2E0235E26447E1387BDD0DEAA942C6BF855A307F7CCA885C8DFCD152E11C628CE1A532163CC6A51CA90EDAD6622B71FD1AFD05613160768B34A77B9D9D0D1E613EB5D93B830E1572A875B52B21130C54B23FA78523A1968F0FFFF7257E608B97C737A09893A3D8E90A31FEA6D71DE5F03A7D435F6DB995F7C32CA7D229CED5C958BE56F8A09F3171AA7818F89AE558F574B7F6BC5F9F21C5C885FC43994C256A5CCBA7A7740A03D1D77AA699805A40E94502BEDD0F51B8D9A28391533E4DEA9D3B2F0382E57764973EF0DE2A2C0C913D3C1BF198BFEC099A4FC42A9E36B6BCE130FAFEF5D20601C45E3E7D3921B14AAA2B119118D1386F976CD4FCAF70F6C4EE5898A6182FAEE50BBC2BE123BCD37ED8AD94208C34E3674B7098F92BEDC5C335902D0F6057A33F1A5F6DE11D3C438D4C3D2CA8EEB5BF684245FDEF3BC7BBFBEB2C828C35C6EF18BD4B2444647059BA7121F495D0AFB39549EA81FC44E6547DF3D07114B408DD366BBCBF4F7C089626B68B5F7364097102A35B7596DA62BEF7B2EA82B11D611EC3C0FDE3239F6243AD167511014CE4427727A8CC74ADB3FC7684FD8E03D0BEDEEFA43CBE65A13E6BFB41F65809CEEA2F112D03EEBEA5BC13A6ADB8D2A72795869CA449F312D155A3AFD13B22776D33B02CC6951D2D8A20F52E2F9CC3F67E860A2CF52FC153D23092BA1BE50B7172A582B70947A909EE92B2AD2E8A9ED6864782983B8CCD9EB4F8053AE8DF5259654A82E33CCD0AFE4571FBD3A7B967DE03BC860AA2D5B5B63B8412900834E826933A2EF178EB3C040EDD678D8479F96EAE8B43C681709F60B789E5191703BEBF65690A28EBF498FA70FC29A045A3CDA262B0F48FECBF317488EBEB66533C1956AE8CB6F138FC831F7F3A4B2CA5E3F3BADA29C5CFA09758038DFE45BF642C0E709F9C589241743964EA5B4ADE59ECAD2885A2854B0B3240DF612C068D9FAB871089A235AD5C54B8B2CF05309FF736262D73940263F1E7B1389EB9C0306552E3172EEBE7BCF7EDC96B399FA297201F1F0EFD807E61871923B76225E02EE71CED20015279975D504C0F0AF5A408307CF60635D474B2B06DBF22F41EDF106D5C3437707FF2CEC48FE071D13967E52C04E6D897C37B0256AA1D535B49871B2F784483FB2BA23DE2AB0A0A3DFE6B0AA4357A7CE7D26423606311F3BAA3254C2C939DE85035EA49CF4A890023B06B9047E4E2F9AB465A87B4D2D17CD2004FF1D0B8A16C14879E9F1661E6434D203E418119DD4E6D8F0B49EE317EB17D18F5921935BFACD5B136B7BC1156E0385F02F004AE036250135AAA7079BE966BE6669E358E70362A148845618CFE8DC5D8170E2B4A8B11D03A20E4D9DC324A106A474CC297BFAC2D3BE9B86479F3C6C708F87685A82DBCB9EE3E5B7013EA567FB3D318818F4D5120B3E14784B1ABD81258200AA65A91FB3015F03F2F00F92F1848FD055617D3407B0CFC6ACB97397782DE96CC79BA615E12BEB1F7AC037139015580170C4BE6133CF70164778A9B065174D5D998B97E0308F6FBB9F8EEEE7A66DE59D0669B02BD490B014186EB0F23AFC4FAC789AEC63D8AC85511670360D4CFDA75B713F450B8CDA9D27C3CC4BEDEAD4F2116F40C589E301F3025A44F726DA9D1EA95F7AA5466D7655333C41D4BA70F731B162185206ACD8BA8F120632C9EA5E6FFE1F6BBE7423929BC6D10A84F22FC478B14180C294A77EFA2B504046AB62830F49F0A5C4A2C706E132B4CE6A362F1A56B6B71AEE35C90710A012C154895B5CA46966FD2D161A6EC1186392897E6786053A0FAF3B2DD8EA2FEE8F2E949D8392C243D40EC1B4C42704147DF42917FB85AA36D5F74633F0E873B55BA3645E065AAD77C82077B0A2AC8E09C5304BD153DF50EEA918798A1213635AF7B33F0095064C0C6BBC53DCA8BA10321FBADC2EA58ECDD3970EDB1FB3D784E4D4A6EF11F9438BAF7B8D0CE4FB7F9B9A48CCA3F19F1A33EDFA24CC7C89CD7DC3295501F00ECCD47ED50E8E92C8341BE92DBBA493CC1A8727F5B2DF67C94DE357D7CCA5E0EE7A676C6A480D8B82B0FFFA9EB042C248B5C0CA5BDC258AC78D22101698A75924D079526A8B737AFC63D352E7106D3DE8A4CD9058BED7A3A9A72E890194A834F0BC7BBE713498DFF1F2DABAE6CF969AC7FF6CB7CD254BBD5710C6C1009B6A919871132E1F66632915F1594838A6C39F58D3412323FBA476602C1D766364521992EF003A5B1D598DD72EF6395373ED285F195EC92D9BC3AC51585188EE314498B7DF9ABF6855F248C180EEFAC986753D47EAF816B04B1223CD162483349EB044FBEC61635EEEAC6806097EAD62A1E593B82D6C07076E1561BB4F161A47FBDB2400379F795B1D7D3F5CE38E0CB640F4248BDDE99FF9398D4BBAD493031BB398B384EB1714716D67086AA1DDEBF4C5686B4403238B55437D574E33B91539C98D1D5FC68C2308F5A93C1E8D27EA9C73915A4E1664B63433768B60F8A0ED7A4E02323E740D64354D039FDEAE234BE22A20FEA025C2E72C559971E044C60FD1C3B9D9DB89CAE47BD3BF3083558AE0078A3BA5C285EA5CFF33BFF23C2770B9B30C3AB21DBF3617BD1B26552D7FCE4C44FF98E2737B987D9CB4289F6E1CC04B756D72DFA2CAF35C2CEF3D9BD64395DEDC1AC013B638CD9138BCE5AAA282D1F97E87664DD6835F8FC9568FDC0F3A2A475C8A71AFCF08E25827A764A9D7E52C58D980E213CE0AF0892A2B9D41E52E5DDB5BAAFB0C4C6D92306EA895212108C57FD27C0386080DA4A791035F89661B39087A2627C96EA6D85600A0D2C191B0A235508D66097F0463827F7879F5125E67F3980FF4946A9D52100C2E6458E0AE60EDF428D5ABAD8051B9647858E3A74924ABF67182CF6C2AD31511AF93639CF1E6BF569D6155E28FD6FB2FC59458E157C9A469571D189EC736106A302BFC0F4012CACBC77EC63EE26F676A382B15134BA2779EADB93A92B6CB820BDFFAAC581654AE53579082E2E7CF181D436E8679E91D076B1C7E2B429E439B35FC4A71487AC52D7E29DFCA7B576FAD4372E1BC59FACE08B9AB68A7905F71F72B80949B43C6A38A9F7DF571F9C55A2796AEFF07CA8B0910A8B0A6BF0535D1A314DD0114CFA2D833288284CD814289253C3C3C3F6426957877E9E11AB38F7DF2A1BC3AC2736D49364FB84A5BC0D98BD77D82A853C2559D58D965875D736DD1DAF692C791F4BCB2F4819C163B7347EF48C4A42C7EBF84EAB49595A9A881D7ACC354889782756EE02F1F22ACF7EE7D26C87B4777C936416F1BC9D72D462295613BA4A19529E677FC7812FF8565DE65ECABA551D55B209F1E786F11EC1F8374C8042214EA3F8F640B08226495944239ADE8C166B715BE807E81EC72B58F6829A02F511955D449F79C3B632CFE8FE87582CFF67CFD3FEC82C47F64F9C8633C84D0C265039A52915C9820A602186BA654AC347998E2456E5C2A54CE147E950B356FD921CC398E2F8381F4A9F94B07C877EE4D38175A308EDDD98B864DD18B6C60F2FC92BAFFAA9C4B411C105893704035C0DB8FC1B3AEC1595A8B13598D7F25D382C234A22B670733AFD80510CED8D5C54701DBA33FC847F514C46FF145D3B768620EE1B845FEC5DEE94455C396AEEE43717939E818D5C620C2851D3BA1C7AF9FF277AC2048D14A571DA43166D680BE7CBDFA4534F8BF3867EE21E82BA10BEE6A784A21F3FD7ABE017F3B5F48DB0AD8EBF8783AD2B84AAC89D62E27BA74678465F18327357443DABC972FBEE96D7C68ECEF2D5F422EC4794CB597E7BA6FD712E06EC82688F3A1B680AD7A704B781456360E36A09F2FA8E6E3856E96FD8AE337A09AD516CB6F8A8986E3FEFFF1FAB4A3F3BD605C10080B867C43D3CCB0382E932591F344F6EA96093B880BBA1B211EF9F00F8459731A187D5FEC9745B4B747FBDF8E847F515E9F25838A4AF9C3020DEDF454E42E7F0928A0";
  private static final DnaString SKNN_DNA_STRING = new DnaString(
      knnSpaceSequence(
          NUM_POINTS, NUM_ATTR, WEIGHT_BITS, COORDINATE_BITS, GUESSFACTOR_BITS),
      SKNN_HEX_STRING);
  private static final double[] WEIGHTS =
      weightsFromDnaString(SKNN_DNA_STRING, NUM_ATTR);
  private static final KdTree<Integer> TREE =
      kdTreeFromDnaString(SKNN_DNA_STRING, WEIGHTS);
  private static final int GUESSFACTORS = power(2, GUESSFACTOR_BITS);

  private DistanceFunction _distFunc;

  public PerceptualGun() {
    super();
    _distFunc = new SquareEuclideanDistanceFunction();
  }

  @Override
  public String getLabel() {
    return "Perceptual Gun";
  }

  @Override
  protected double aimInternal(Wave w, boolean painting) {
    double[] p = new double[] {
      Math.abs(w.targetVelocity) / 8.0,
      Math.min(w.targetDistance / w.bulletSpeed(), 90) / 90.0,
      Math.sin(w.targetRelativeHeading),
      (Math.cos(w.targetRelativeHeading) + 1) / 2,
      (w.targetAccel + Rules.DECELERATION)
          / (Rules.DECELERATION + Rules.ACCELERATION),
      Math.min(1.25, w.targetWallDistance) / 1.25,
      Math.min(1.15, w.targetRevWallDistance) / 1.15,
    };
    double[] wp = weightedPoint(p, WEIGHTS);

    int guessFactorIndex = TREE.findNearestNeighbors(wp, 1, _distFunc).getMax();
    double guessFactor;
    if (GUESSFACTORS % 2 == 0) {
      if (guessFactorIndex == (GUESSFACTORS / 2)
          || guessFactorIndex == (GUESSFACTORS / 2 - 1)) {
        guessFactor = 0;
      } else {
        guessFactor = ((double) (guessFactorIndex - (GUESSFACTORS / 2) + 1))
            / (GUESSFACTORS / 2);
      }
    } else {
      guessFactor = ((double) (guessFactorIndex - (GUESSFACTORS / 2)))
          / (GUESSFACTORS / 2);
    }

    return w.absBearing + (w.orbitDirection
        * guessFactor * w.preciseEscapeAngle(guessFactor >= 0));
  }

  public static DnaSequence knnSpaceSequence(int numPoints, int numAttr,
      int weightBits, int coordBits, int guessFactorBits) {
    DnaSequence treeSequence = new DnaSequence();
    for (int x = 0; x < numAttr; x++) {
      treeSequence.addGene(new Gene("w" + (x + 1), weightBits));
    }
    for (int x = 0; x < numPoints; x++) {
      for (int y = 0; y < numAttr; y++) {
        treeSequence.addGene(
            new Gene("p" + (x + 1) + "x" + (y + 1), coordBits));
      }
      treeSequence.addGene(new Gene("r" + (x + 1), guessFactorBits));
    }

    return treeSequence;
  }

  public static KdTree<Integer> kdTreeFromDnaString(
      DnaString dnaString, double[] weights) {

    int numAttr = weights.length;
    KdTree<Integer> tree = new KdTree<Integer>(numAttr);

    String bitString = dnaString.bitString();
    int numPoints = (bitString.length() - (numAttr * WEIGHT_BITS))
        / ((numAttr * COORDINATE_BITS) + GUESSFACTOR_BITS);
    int coordinateValues = maxValueFromBits(COORDINATE_BITS);

    for (int x = 0; x < numPoints; x++) {
      double[] p = new double[numAttr];
      for (int y = 0; y < numAttr; y++) {
        p[y] = (((double) dnaString.getNumber("p" + (x + 1) + "x" + (y + 1)))
            / coordinateValues) * weights[y];
      }
      tree.addPoint(p, (int) dnaString.getNumber("r" + (x + 1)));
    }

    return tree;
  }

  public static double[] weightsFromDnaString(
      DnaString dnaString, int numAttr) {
    double[] weights = new double[numAttr];
    for (int x = 0; x < numAttr; x++) {
      weights[x] = ((double) dnaString.getNumber("w" + (x + 1)))
          / maxValueFromBits(WEIGHT_BITS);
    }
    return weights;
  }

  private static int maxValueFromBits(int numBits) {
    return (int) Math.round(DiaUtils.power(2, numBits) - 1);
  }

  public static int power(int base, int exp) {
    int retVal = 1;
    for (int x = 0; x < exp; x++) {
      retVal *= base;
    }

    return retVal;
  }

  public static double[] weightedPoint(double[] p, double[] weights) {
    double[] wp = new double[p.length];
    for (int x = 0; x < wp.length; x++) {
      wp[x] = p[x] * weights[x];
    }

    return wp;
  }

  @Override
  public List<KnnView<T>> newDataViews() {
    return new ArrayList<KnnView<T>>();
  }
}
