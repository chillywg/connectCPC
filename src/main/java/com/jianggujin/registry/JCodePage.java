/**
 * Copyright 2018 jianggujin (www.jianggujin.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jianggujin.registry;

import java.util.HashMap;
import java.util.Map;

/**
 * 代码页
 * 
 * @author jianggujin
 *
 */
public class JCodePage {
   private static Map<String, String> codePageMap = new HashMap<String, String>();
   static {
      codePageMap.put("37", "IBM037");
      codePageMap.put("437", "IBM437");
      codePageMap.put("500", "IBM500");
      codePageMap.put("708", "ASMO-708");
      codePageMap.put("720", "DOS-720");
      codePageMap.put("737", "ibm737");
      codePageMap.put("775", "ibm775");
      codePageMap.put("850", "ibm850");
      codePageMap.put("852", "ibm852");
      codePageMap.put("855", "IBM855");
      codePageMap.put("857", "ibm857");
      codePageMap.put("858", "IBM00858");
      codePageMap.put("860", "IBM860");
      codePageMap.put("861", "ibm861");
      codePageMap.put("862", "DOS-862");
      codePageMap.put("863", "IBM863");
      codePageMap.put("864", "IBM864");
      codePageMap.put("865", "IBM865");
      codePageMap.put("866", "cp866");
      codePageMap.put("869", "ibm869");
      codePageMap.put("870", "IBM870");
      codePageMap.put("874", "windows-874");
      codePageMap.put("875", "cp875");
      codePageMap.put("932", "shift_jis");
      codePageMap.put("936", "gb2312");
      codePageMap.put("949", "ks_c_5601-1987");
      codePageMap.put("950", "big5");
      codePageMap.put("1026", "IBM1026");
      codePageMap.put("1047", "IBM01047");
      codePageMap.put("1140", "IBM01140");
      codePageMap.put("1141", "IBM01141");
      codePageMap.put("1142", "IBM01142");
      codePageMap.put("1143", "IBM01143");
      codePageMap.put("1144", "IBM01144");
      codePageMap.put("1145", "IBM01145");
      codePageMap.put("1146", "IBM01146");
      codePageMap.put("1147", "IBM01147");
      codePageMap.put("1148", "IBM01148");
      codePageMap.put("1149", "IBM01149");
      codePageMap.put("1200", "utf-16");
      codePageMap.put("1201", "unicodeFFFE");
      codePageMap.put("1250", "windows-1250");
      codePageMap.put("1251", "windows-1251");
      codePageMap.put("1252", "Windows-1252");
      codePageMap.put("1253", "windows-1253");
      codePageMap.put("1254", "windows-1254");
      codePageMap.put("1255", "windows-1255");
      codePageMap.put("1256", "windows-1256");
      codePageMap.put("1257", "windows-1257");
      codePageMap.put("1258", "windows-1258");
      codePageMap.put("1361", "Johab");
      codePageMap.put("10000", "macintosh");
      codePageMap.put("10001", "x-mac-japanese");
      codePageMap.put("10002", "x-mac-chinesetrad");
      codePageMap.put("10003", "x-mac-korean");
      codePageMap.put("10004", "x-mac-arabic");
      codePageMap.put("10005", "x-mac-hebrew");
      codePageMap.put("10006", "x-mac-greek");
      codePageMap.put("10007", "x-mac-cyrillic");
      codePageMap.put("10008", "x-mac-chinesesimp");
      codePageMap.put("10010", "x-mac-romanian");
      codePageMap.put("10017", "x-mac-ukrainian");
      codePageMap.put("10021", "x-mac-thai");
      codePageMap.put("10029", "x-mac-ce");
      codePageMap.put("10079", "x-mac-icelandic");
      codePageMap.put("10081", "x-mac-turkish");
      codePageMap.put("10082", "x-mac-croatian");
      codePageMap.put("20000", "x-Chinese-CNS");
      codePageMap.put("20001", "x-cp20001");
      codePageMap.put("20002", "x-Chinese-Eten");
      codePageMap.put("20003", "x-cp20003");
      codePageMap.put("20004", "x-cp20004");
      codePageMap.put("20005", "x-cp20005");
      codePageMap.put("20105", "x-IA5");
      codePageMap.put("20106", "x-IA5-German");
      codePageMap.put("20107", "x-IA5-Swedish");
      codePageMap.put("20108", "x-IA5-Norwegian");
      codePageMap.put("20127", "us-ascii");
      codePageMap.put("20261", "x-cp20261");
      codePageMap.put("20269", "x-cp20269");
      codePageMap.put("20273", "IBM273");
      codePageMap.put("20277", "IBM277");
      codePageMap.put("20278", "IBM278");
      codePageMap.put("20280", "IBM280");
      codePageMap.put("20284", "IBM284");
      codePageMap.put("20285", "IBM285");
      codePageMap.put("20290", "IBM290");
      codePageMap.put("20297", "IBM297");
      codePageMap.put("20420", "IBM420");
      codePageMap.put("20423", "IBM423");
      codePageMap.put("20424", "IBM424");
      codePageMap.put("20833", "x-EBCDIC-KoreanExtended");
      codePageMap.put("20838", "IBM-Thai");
      codePageMap.put("20866", "koi8-r");
      codePageMap.put("20871", "IBM871");
      codePageMap.put("20880", "IBM880");
      codePageMap.put("20905", "IBM905");
      codePageMap.put("20924", "IBM00924");
      codePageMap.put("20932", "EUC-JP");
      codePageMap.put("20936", "x-cp20936");
      codePageMap.put("20949", "x-cp20949");
      codePageMap.put("21025", "cp1025");
      codePageMap.put("21866", "koi8-u");
      codePageMap.put("28591", "iso-8859-1");
      codePageMap.put("28592", "iso-8859-2");
      codePageMap.put("28593", "iso-8859-3");
      codePageMap.put("28594", "iso-8859-4");
      codePageMap.put("28595", "iso-8859-5");
      codePageMap.put("28596", "iso-8859-6");
      codePageMap.put("28597", "iso-8859-7");
      codePageMap.put("28598", "iso-8859-8");
      codePageMap.put("28599", "iso-8859-9");
      codePageMap.put("28603", "iso-8859-13");
      codePageMap.put("28605", "iso-8859-15");
      codePageMap.put("29001", "x-Europa");
      codePageMap.put("38598", "iso-8859-8-i");
      codePageMap.put("50220", "iso-2022-jp");
      codePageMap.put("50221", "csISO2022JP");
      codePageMap.put("50222", "iso-2022-jp");
      codePageMap.put("50225", "iso-2022-kr");
      codePageMap.put("50227", "x-cp50227");
      codePageMap.put("51932", "euc-jp");
      codePageMap.put("51936", "EUC-CN");
      codePageMap.put("51949", "euc-kr");
      codePageMap.put("52936", "hz-gb-2312");
      codePageMap.put("54936", "GB18030");
      codePageMap.put("57002", "x-iscii-de");
      codePageMap.put("57003", "x-iscii-be");
      codePageMap.put("57004", "x-iscii-ta");
      codePageMap.put("57005", "x-iscii-te");
      codePageMap.put("57006", "x-iscii-as");
      codePageMap.put("57007", "x-iscii-or");
      codePageMap.put("57008", "x-iscii-ka");
      codePageMap.put("57009", "x-iscii-ma");
      codePageMap.put("57010", "x-iscii-gu");
      codePageMap.put("57011", "x-iscii-pa");
      codePageMap.put("65000", "utf-7");
      codePageMap.put("65001", "utf-8");
      codePageMap.put("65005", "utf-32");
      codePageMap.put("65006", "utf-32BE");
   }

   /**
    * 获得字符集
    * 
    * @param codePage
    * @return
    */
   public static String getCharset(String codePage) {
      return codePageMap.get(codePage);
   }
}
