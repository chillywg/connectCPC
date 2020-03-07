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

/**
 * 添加选项
 * 
 * @author jianggujin
 * 
 */
public class JAddOptions implements JOptions {
   private boolean v = false;
   private String valueName;

   private boolean ve = false;

   private JValueType type;
   private boolean t = false;

   private Character separator;
   private boolean s = false;

   private boolean d = false;
   private String data;

   private boolean f = false;

   /**
    * 所选项之下要添加的值名
    * 
    * @param valueName
    */
   public JAddOptions useV(String valueName) {
      this.v = true;
      this.valueName = valueName;
      this.ve = false;
      return this;
   }

   /**
    * 为注册表项添加空白值名(默认)
    */
   public JAddOptions useVE() {
      this.ve = true;
      this.v = false;
      return this;
   }

   /**
    * RegKey 数据类型，如果忽略，则采用 REG_SZ
    * 
    * @param type
    * @return
    */
   public JAddOptions useT(JValueType type) {
      this.t = true;
      this.type = type;
      return this;
   }

   /**
    * 指定一个在 REG_MULTI_SZ 数据字符串中用作分隔符的字符 如果忽略，则将 "\0" 用作分隔符
    * 
    * @param separator
    * @return
    */
   public JAddOptions useS(Character separator) {
      this.separator = separator;
      this.s = true;
      return this;
   }

   /**
    * 要分配给添加的注册表 ValueName 的数据
    * 
    * @param data
    * @return
    */
   public JAddOptions useD(String data) {
      this.d = true;
      this.data = data;
      return this;
   }

   /**
    * 不用提示就强行覆盖现有注册表项
    * 
    * @return
    */
   public JAddOptions useF() {
      this.f = true;
      return this;
   }

   @Override
   public String toOptions() {
      StringBuilder builder = new StringBuilder();
      if (this.v && this.valueName != null && this.valueName.length() > 0) {
         builder.append("/v ").append(this.valueName);
      }
      if (this.ve) {
         if (builder.length() > 0) {
            builder.append(" ");
         }
         builder.append("/ve");
      }

      if (this.t && this.type != null) {
         if (builder.length() > 0) {
            builder.append(" ");
         }
         return "/t " + type.name();
      }

      if (this.s && this.separator != null) {
         if (builder.length() > 0) {
            builder.append(" ");
         }
         return "/s " + separator;
      }

      if (this.d && this.data != null && this.data.length() > 0) {
         builder.append("/d ").append(this.data);
      }
      if (this.f) {
         if (builder.length() > 0) {
            builder.append(" ");
         }
         builder.append("/f");
      }

      return builder.toString();
   }

}
