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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 注册表工具
 * 
 * @author jianggujin
 *
 */
public class JRegistry {
   /**
    * 控制台输出流编码，如果出现乱码可以设置
    */
   public static String CMD_ENCODING = null;
   public final static String DEFAULT_CMD_ENCODING = "GBK";

   /**
    * 查询
    * 
    * @param keyName
    *           [\\Machine\]FullKey； Machine - 远程机器名称，省略当前机器的默认值。在远程机器上 只有 HKLM
    *           和 HKU 可用； FullKey - 以 ROOTKEY\SubKey 名称形式； ROOTKEY - [ HKLM |
    *           HKCU | HKCR | HKU | HKCC ]； SubKey - 在选择的 ROOTKEY 下的注册表项的全名
    * @param options
    * @return
    * @throws IOException
    * @throws InterruptedException
    */
   public static JExecResult query(String keyName, JQueryOptions options) throws IOException, InterruptedException {
      return exec("query", keyName, options);
   }

   /**
    * 添加
    * 
    * @param keyName
    *           [\\Machine\]FullKey； Machine 远程机器名 - 忽略默认到当前机器。远程机器上 只有 HKLM 和
    *           HKU 可用； FullKey ROOTKEY\SubKey； ROOTKEY [ HKLM | HKCU | HKCR |
    *           HKU | HKCC ]； SubKey 所选 ROOTKEY 下注册表项的完整名称
    * @param options
    * @return
    * @throws IOException
    * @throws InterruptedException
    */
   public static JExecResult add(String keyName, JAddOptions options) throws IOException, InterruptedException {
      return exec("add", keyName, options);
   }

   /**
    * 删除
    * 
    * @param keyName
    *           [\\Machine\]FullKey 远程机器名 - 如果省略，默认情况下将使用当前机 远程机器上只有 HKLM 和 HKU
    *           可用。 FullKey ROOTKEY\SubKey ROOTKEY [ HKLM | HKCU | HKCR | HKU |
    *           HKCC ] SubKey 所选 ROOTKEY 下面的注册表项的全名。
    * @param options
    * @return
    * @throws IOException
    * @throws InterruptedException
    */
   public static JExecResult delete(String keyName, JDeleteOptions options) throws IOException, InterruptedException {
      return exec("delete", keyName, options);
   }

   /**
    * 通用操作
    * 
    * @param operation
    * @param keyName
    * @param options
    * @return
    * @throws IOException
    * @throws InterruptedException
    */
   private static JExecResult exec(String operation, String keyName, JOptions options)
         throws IOException, InterruptedException {
      List<String> cmds = new ArrayList<String>();
      cmds.add("reg");
      cmds.add(operation);
      cmds.add(keyName);
      if (options != null) {
         String opts = options.toOptions();
         if (opts.length() > 0) {
            cmds.add(opts);
         }
      }
      return exec(true, cmds.toArray(new String[0]));
   }

   /**
    * 复制
    * 
    * @param keyName1
    *           [\\Machine\]FullKey； Machine 远程机器名 - 如果省略，默认情况下将使用当前机器。 远程机器上只有
    *           HKLM 和 HKU 可用。； FullKey ROOTKEY\SubKey； ROOTKEY [ HKLM | HKCU |
    *           HKCR | HKU | HKCC ]； SubKey 所选 ROOTKEY 下的注册表项的全名。
    * @param keyName2
    *           [\\Machine\]FullKey； Machine 远程机器名 - 如果省略，默认情况下将使用当前机器。 远程机器上只有
    *           HKLM 和 HKU 可用。； FullKey ROOTKEY\SubKey； ROOTKEY [ HKLM | HKCU |
    *           HKCR | HKU | HKCC ]； SubKey 所选 ROOTKEY 下的注册表项的全名。
    * @param options
    * @return
    * @throws IOException
    * @throws InterruptedException
    */
   public static JExecResult copy(String keyName1, String keyName2, JCopyOptions options)
         throws IOException, InterruptedException {
      List<String> cmds = new ArrayList<String>();
      cmds.add("reg");
      cmds.add("copy");
      cmds.add(keyName1);
      cmds.add(keyName2);
      if (options != null) {
         String opts = options.toOptions();
         if (opts.length() > 0) {
            cmds.add(opts);
         }
      }
      return exec(true, cmds.toArray(new String[0]));
   }

   /**
    * 保存
    * 
    * @param keyName
    *           ROOTKEY\SubKey; ROOTKEY [ HKLM | HKCU | HKCR | HKU | HKCC ];
    *           SubKey 所选 ROOTKEY 下的注册表项的全名
    * @param filName
    *           要保存的磁盘文件名。如果没有指定路径，文件会在调用进程的 当前文件夹中得到创建
    * @param useY
    *           不用提示就强行覆盖现有文件
    * @return
    * @throws IOException
    * @throws InterruptedException
    */
   public static JExecResult save(String keyName, String filName, boolean useY)
         throws IOException, InterruptedException {
      List<String> cmds = new ArrayList<String>();
      cmds.add("reg");
      cmds.add("save");
      cmds.add(keyName);
      cmds.add(filName);
      if (useY) {
         cmds.add("/y");
      }
      return exec(true, cmds.toArray(new String[0]));
   }

   /**
    * 恢复
    * 
    * @param keyName
    *           ROOTKEY\SubKey (只是本地机器); ROOTKEY [ HKLM | HKCU | HKCR | HKU |
    *           HKCC ]; SubKey 要将配置单元文件还原到的注册表项全名。 覆盖现有项的值和子项
    * @param filName
    *           要还原的配置单元文件名。 你必须使用{@link #save(String, String, boolean)}来创建这个文件
    * @return
    * @throws IOException
    * @throws InterruptedException
    */
   public static JExecResult restore(String keyName, String filName) throws IOException, InterruptedException {
      List<String> cmds = new ArrayList<String>();
      cmds.add("reg");
      cmds.add("restore");
      cmds.add(keyName);
      cmds.add(filName);
      return exec(true, cmds.toArray(new String[0]));
   }

   /**
    * 加载
    * 
    * @param keyName
    *           ROOTKEY\SubKey (只是本地机器) ; ROOTKEY [ HKLM | HKCU | HKCR | HKU |
    *           HKCC ] ; SubKey 要将配置单元文件还原到的注册表项全名。 覆盖现有项的值和子项
    * @param filName
    *           要加载的配置单元文件名。你必须使用{@link #save(String, String, boolean)}来创建这个文件
    * @return
    * @throws IOException
    * @throws InterruptedException
    */
   public static JExecResult load(String keyName, String filName) throws IOException, InterruptedException {
      List<String> cmds = new ArrayList<String>();
      cmds.add("reg");
      cmds.add("load");
      cmds.add(keyName);
      cmds.add(filName);
      return exec(true, cmds.toArray(new String[0]));
   }

   /**
    * 卸载
    * 
    * @param keyName
    *           ROOTKEY\SubKey (只是本地机器); ROOTKEY [ HKLM | HKU ]; SubKey
    *           要卸载的配置单元的注册表项名称
    * @return
    * @throws IOException
    * @throws InterruptedException
    */
   public static JExecResult unload(String keyName) throws IOException, InterruptedException {
      List<String> cmds = new ArrayList<String>();
      cmds.add("reg");
      cmds.add("unload");
      cmds.add(keyName);
      return exec(true, cmds.toArray(new String[0]));
   }

   /**
    * 比较
    * 
    * @param keyName1
    *           [\\Machine\]FullKey; Machine 远程机器名 - 如果省略，默认情况下将使用当前机器。 远程机器上只有
    *           HKLM 和 HKU 可用; FullKey ROOTKEY\SubKey； ROOTKEY [ HKLM | HKCU |
    *           HKCR | HKU | HKCC ]； SubKey 所选 ROOTKEY 下的注册表项的全名
    * 
    * @param keyName2
    *           [\\Machine\]FullKey； Machine 远程机器名 - 如果省略，默认情况下将使用当前机器。 远程机器上只有
    *           HKLM 和 HKU 可用； FullKey ROOTKEY\SubKey； ROOTKEY [ HKLM | HKCU |
    *           HKCR | HKU | HKCC ]； SubKey 所选 ROOTKEY 下的注册表项的全名。
    * @param options
    * @return 每个输出行前面显示的符号定义为: = 表示 FullKey1 等于 FullKey2 数据 &lt; 指的是 FullKey1
    *         数据，与 FullKey2 数据不同 &gt; 指的是 FullKey2 数据，与 Fullkey1 数据不同
    * @throws IOException
    * @throws InterruptedException
    */
   public static JExecResult compare(String keyName1, String keyName2, JCompareOptions options)
         throws IOException, InterruptedException {
      List<String> cmds = new ArrayList<String>();
      cmds.add("reg");
      cmds.add("compare");
      cmds.add(keyName1);
      cmds.add(keyName2);
      if (options != null) {
         String opts = options.toOptions();
         if (opts.length() > 0) {
            cmds.add(opts);
         }
      }
      JExecResult result = exec(true, cmds.toArray(new String[0]));
      result.setSuccess(result.getExitValue() == 0 || result.getExitValue() == 2);
      return result;
   }

   /**
    * 保存
    * 
    * @param keyName
    *           ROOTKEY[\SubKey] (只是本地机器)； ROOTKEY [ HKLM | HKCU | HKCR | HKU |
    *           HKCC ]； SubKey 所选 ROOTKEY 下的注册表项的全名
    * @param filName
    *           要导出的磁盘文件名
    * @param useY
    *           不用提示就强行覆盖现有文件
    * @return
    * @throws IOException
    * @throws InterruptedException
    */
   public static JExecResult export(String keyName, String filName, boolean useY)
         throws IOException, InterruptedException {
      List<String> cmds = new ArrayList<String>();
      cmds.add("reg");
      cmds.add("export");
      cmds.add(keyName);
      cmds.add(filName);
      if (useY) {
         cmds.add("/y");
      }
      return exec(true, cmds.toArray(new String[0]));
   }

   /**
    * 导入
    * 
    * @param filName
    *           要导入的磁盘文件名(只是本地机器)
    * @return
    * @throws IOException
    * @throws InterruptedException
    */
   public static JExecResult import2(String filName) throws IOException, InterruptedException {
      List<String> cmds = new ArrayList<String>();
      cmds.add("reg");
      cmds.add("import");
      cmds.add(filName);
      return exec(true, cmds.toArray(new String[0]));
   }

   public static void dump(JExecResult result) {
      System.out.println("Exec Result: " + result.isSuccess());
      System.out.println("Lines:");
      for (String line : result.getLines()) {
         System.out.println(line);
      }
   }

   private static String queryCodePage() throws IOException, InterruptedException {

      List<String> cmds = new ArrayList<String>();
      cmds.add("reg");
      cmds.add("query");
      cmds.add("HKEY_CURRENT_USER\\Console");
      JQueryOptions options = new JQueryOptions().useF("CodePage").useS();
      if (options != null) {
         String opts = options.toOptions();
         if (opts.length() > 0) {
            cmds.add(opts);
         }
      }

      JExecResult result = exec0(true, cmds.toArray(new String[0]));
      String[] lines = null;
      boolean canParse = false;
      if (result.isSuccess() && (lines = result.getLines()).length > 2) {
         for (String line : lines) {
            if (!canParse && line.startsWith("HKEY_CURRENT_USER\\Console") && line.endsWith("cmd.exe")) {
               canParse = true;
            }
            if (canParse && line.trim().startsWith("CodePage")) {
               int index = line.lastIndexOf("0x");
               String hex = line.substring(index + 2);
               return JCodePage.getCharset(Integer.parseInt(hex, 16) + "");
            }
         }
      }
      return null;
   }

   private static void ensureEncoding() {
      if (CMD_ENCODING == null) {
         synchronized (JRegistry.class) {
            if (CMD_ENCODING == null) {
               try {
                  // 如果修改过cmd属性，可以查到注册表中的相关配置，所以先查询代码页，获得控制台编码
                  CMD_ENCODING = queryCodePage();
               } catch (Exception e) {
                  CMD_ENCODING = DEFAULT_CMD_ENCODING;
               }
            }
         }
      }
   }

   private static JExecResult exec(boolean skipEmptyLine, String[] cmds) throws IOException, InterruptedException {
      ensureEncoding();
      return exec0(skipEmptyLine, cmds);
   }

   private static JExecResult exec0(boolean skipEmptyLine, String[] cmds) throws IOException, InterruptedException {
      StringBuilder cmd = new StringBuilder();
      boolean first = true;
      for (String item : cmds) {
         if (!first) {
            cmd.append(" ");
         }
         cmd.append(item);
         first = false;
      }
      Process process = Runtime.getRuntime().exec(cmd.toString());
      int exitVal = process.waitFor();
      // 读取屏幕输出
      BufferedReader reader = new BufferedReader(
            new InputStreamReader(exitVal == 0 ? process.getInputStream() : process.getErrorStream(),
                  CMD_ENCODING == null ? DEFAULT_CMD_ENCODING : CMD_ENCODING));
      String line = null;
      List<String> lines = new ArrayList<String>();
      while ((line = reader.readLine()) != null) {
         if (line.length() > 0) {
            lines.add(line);
         }
      }
      reader.close();
      return new JExecResult(exitVal, lines.toArray(new String[0]));
   }
}
