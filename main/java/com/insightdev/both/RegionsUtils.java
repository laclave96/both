package com.insightdev.both;

import org.json.JSONException;
import org.json.JSONObject;

public class RegionsUtils {

    private static final String jsonStr = "{\"AF\":\"Asia\",\"AX\":\"Europe\",\"AL\":\"Europe\",\"DZ\":\"Africa\",\"AS\":\"North America\",\"AD\":\"Europe\",\"AO\":\"Africa\",\"AI\":\"South America\",\"AQ\":\"North America\",\"AG\":\"South America\",\"AR\":\"South America\",\"AM\":\"Asia\",\"AW\":\"South America\",\"AU\":\"North America\",\"AT\":\"Europe\",\"AZ\":\"Asia\",\"BS\":\"South America\",\"BH\":\"Asia\",\"BD\":\"Asia\",\"BB\":\"South America\",\"BY\":\"Europe\",\"BE\":\"Europe\",\"BZ\":\"South America\",\"BJ\":\"Africa\",\"BM\":\"North America\",\"BT\":\"Asia\",\"BO\":\"South America\",\"BQ\":\"South America\",\"BA\":\"Europe\",\"BW\":\"Africa\",\"BV\":\"South America\",\"BR\":\"South America\",\"IO\":\"Asia\",\"VG\":\"South America\",\"BN\":\"Asia\",\"BG\":\"Europe\",\"BF\":\"Africa\",\"BI\":\"Africa\",\"KH\":\"Asia\",\"CM\":\"Africa\",\"CA\":\"North America\",\"CV\":\"Africa\",\"KY\":\"South America\",\"CF\":\"Africa\",\"TD\":\"Africa\",\"CL\":\"South America\",\"CN\":\"Asia\",\"CX\":\"Asia\",\"CC\":\"Asia\",\"CO\":\"South America\",\"KM\":\"Africa\",\"CD\":\"Africa\",\"CG\":\"Africa\",\"CK\":\"North America\",\"CR\":\"South America\",\"CI\":\"Africa\",\"HR\":\"Europe\",\"CU\":\"South America\",\"CW\":\"South America\",\"CY\":\"Asia\",\"CZ\":\"Europe\",\"DK\":\"Europe\",\"DJ\":\"Africa\",\"DM\":\"South America\",\"DO\":\"South America\",\"EC\":\"South America\",\"EG\":\"Africa\",\"SV\":\"South America\",\"GQ\":\"Africa\",\"ER\":\"Africa\",\"EE\":\"Europe\",\"ET\":\"Africa\",\"FO\":\"Europe\",\"FK\":\"South America\",\"FJ\":\"North America\",\"FI\":\"Europe\",\"FR\":\"Europe\",\"GF\":\"South America\",\"PF\":\"North America\",\"TF\":\"North America\",\"GA\":\"Africa\",\"GM\":\"Africa\",\"GE\":\"Asia\",\"DE\":\"Europe\",\"GH\":\"Africa\",\"GI\":\"Europe\",\"GR\":\"Europe\",\"GL\":\"North America\",\"GD\":\"Europe\",\"GP\":\"Europe\",\"GU\":\"Asia\",\"GT\":\"South America\",\"GG\":\"Europe\",\"GN\":\"Africa\",\"GW\":\"Africa\",\"GY\":\"South America\",\"HT\":\"South America\",\"HM\":\"North America\",\"VA\":\"Europe\",\"HN\":\"South America\",\"HK\":\"Asia\",\"HU\":\"Europe\",\"IS\":\"Europe\",\"IN\":\"Asia\",\"ID\":\"Asia\",\"IR\":\"Asia\",\"IQ\":\"Asia\",\"IE\":\"Europe\",\"IM\":\"Europe\",\"IL\":\"Asia\",\"IT\":\"Europe\",\"JM\":\"South America\",\"JP\":\"Asia\",\"JE\":\"Europe\",\"JO\":\"Asia\",\"KZ\":\"Asia\",\"KE\":\"Africa\",\"KI\":\"North America\",\"KP\":\"Asia\",\"KR\":\"Asia\",\"KW\":\"Asia\",\"KG\":\"Asia\",\"LA\":\"Asia\",\"LV\":\"Europe\",\"LB\":\"Asia\",\"LS\":\"Africa\",\"LR\":\"Africa\",\"LY\":\"Africa\",\"LI\":\"Europe\",\"LT\":\"Europe\",\"LU\":\"Europe\",\"MO\":\"Asia\",\"MK\":\"Europe\",\"MG\":\"Africa\",\"MW\":\"Africa\",\"MY\":\"Asia\",\"MV\":\"Asia\",\"ML\":\"Africa\",\"MT\":\"Europe\",\"MH\":\"North America\",\"MQ\":\"South America\",\"MR\":\"Africa\",\"MU\":\"Africa\",\"YT\":\"Africa\",\"MX\":\"South America\",\"FM\":\"North America\",\"MD\":\"Europe\",\"MC\":\"Europe\",\"MN\":\"Asia\",\"ME\":\"Europe\",\"MS\":\"South America\",\"MA\":\"Africa\",\"MZ\":\"Africa\",\"MM\":\"Asia\",\"NA\":\"Africa\",\"NR\":\"North America\",\"NP\":\"Asia\",\"NL\":\"Europe\",\"NC\":\"North America\",\"NZ\":\"North America\",\"NI\":\"South America\",\"NE\":\"Africa\",\"NG\":\"Africa\",\"NU\":\"North America\",\"NF\":\"North America\",\"MP\":\"North America\",\"NO\":\"Europe\",\"OM\":\"Asia\",\"PK\":\"Asia\",\"PW\":\"Asia\",\"PS\":\"Asia\",\"PA\":\"South America\",\"PG\":\"North America\",\"PY\":\"South America\",\"PE\":\"South America\",\"PH\":\"Asia\",\"PN\":\"North America\",\"PL\":\"Europe\",\"PT\":\"Europe\",\"PR\":\"South America\",\"QA\":\"Asia\",\"RE\":\"Africa\",\"RO\":\"Europe\",\"RU\":\"Europe\",\"RW\":\"Africa\",\"BL\":\"South America\",\"SH\":\"Africa\",\"KN\":\"South America\",\"LC\":\"South America\",\"MF\":\"South America\",\"PM\":\"North America\",\"VC\":\"South America\",\"WS\":\"North America\",\"SM\":\"Europe\",\"ST\":\"Africa\",\"SA\":\"Asia\",\"SN\":\"Africa\",\"RS\":\"Europe\",\"SC\":\"Africa\",\"SL\":\"Africa\",\"SG\":\"Asia\",\"SX\":\"South America\",\"SK\":\"Europe\",\"SI\":\"Europe\",\"SB\":\"North America\",\"SO\":\"Africa\",\"ZA\":\"Africa\",\"GS\":\"North America\",\"SS\":\"Africa\",\"ES\":\"Europe\",\"LK\":\"Asia\",\"SD\":\"Africa\",\"SR\":\"South America\",\"SJ\":\"Europe\",\"SZ\":\"Africa\",\"SE\":\"Europe\",\"CH\":\"Europe\",\"SY\":\"Asia\",\"TW\":\"Asia\",\"TJ\":\"Asia\",\"TZ\":\"Africa\",\"TH\":\"Asia\",\"TL\":\"Asia\",\"TG\":\"Africa\",\"TK\":\"North America\",\"TO\":\"North America\",\"TT\":\"South America\",\"TN\":\"Africa\",\"TR\":\"Asia\",\"TM\":\"Asia\",\"TC\":\"South America\",\"TV\":\"North America\",\"UG\":\"Africa\",\"UA\":\"Europe\",\"AE\":\"Asia\",\"GB\":\"Europe\",\"US\":\"North America\",\"UM\":\"North America\",\"VI\":\"North America\",\"UY\":\"South America\",\"UZ\":\"Asia\",\"VU\":\"North America\",\"VE\":\"South America\",\"VN\":\"Asia\",\"WF\":\"North America\",\"EH\":\"Africa\",\"YE\":\"Asia\",\"ZM\":\"Africa\",\"ZW\":\"Africa\"}";


    public static String getRegion(String countryCode) {

        JSONObject json;
        try {
            json = new JSONObject(jsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        String region;

        try {
            region = json.getString(countryCode);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        switch (region) {

            case "South America":
                return "1";

            case "North America":
                return "2";

            case "Europe":
                return "3";

            case "Asia":
                return "4";

            case "Africa":
                return "5";

            default:
                return null;

        }

    }

}
