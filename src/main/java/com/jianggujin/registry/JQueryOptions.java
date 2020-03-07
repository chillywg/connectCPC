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
 * 查询选项
 * 
 * @author jianggujin
 * 
 */
public class JQueryOptions implements JOptions {
   // 值相关选项
   private boolean v = false;
   private String valueName;
   private boolean ve = false;

   // 搜索相关查询选项
   private String data;
   private boolean f = false, k = false, d = false, c = false, e = false;

   // 值循环查询所有子项和值
   private boolean s = false;

   // 指定注册表值数据类型
   private JValueType type;
   private boolean t = false;

   // 详细: 显示值名称类型的数字等值
   private boolean z = false;

   // 为 REG_MULTI_SZ 在数据字符串中指定分隔符(长度只为 1 个字符)。 默认分隔符为 "\0"
   private Character separator;
   private boolean se = false;

   /**
    * 具体的注册表项值的查询。 如果省略，会查询该项的所有值
    * 
    * @param valueName
    */
   public JQueryOptions useV(String valueName) {
      this.v = true;
      this.valueName = valueName;
      this.ve = false;
      return this;
   }

   /**
    * 查询默认值或空值名称(默认)
    */
   public JQueryOptions useVE() {
      this.ve = true;
      this.v = false;
      return this;
   }

   /**
    * 指定搜索的数据或模式。 如果字符串包含空格，请使用双引号。默认为 "*"。
    * 
    * @param data
    */
   public JQueryOptions useF(String data) {
      this.f = true;
      this.data = data;
      return this;
   }

   /**
    * 指定只在项名称中搜索，需要执行{@link #useF(String)}
    * 
    * @return
    */
   public JQueryOptions useK() {
      this.k = true;
      return this;
   }

   /**
    * 指定只在数据中搜索，需要执行{@link #useF(String)}
    * 
    * @return
    */
   public JQueryOptions useD() {
      this.d = true;
      return this;
   }

   /**
    * 指定搜索时区分大小写。 默认搜索为不区分大小写，需要执行{@link #useF(String)}
    * 
    * @return
    */
   public JQueryOptions useC() {
      this.c = true;
      return this;
   }

   /**
    * 指定只返回完全匹配。 默认是返回所有匹配，需要执行{@link #useF(String)}
    * 
    * @return
    */
   public JQueryOptions useE() {
      this.e = true;
      return this;
   }

   /**
    * 循环查询所有子项和值
    * 
    * @return
    */
   public JQueryOptions useS() {
      this.s = true;
      return this;
   }

   /**
    * 指定注册表值数据类型。 默认为所有类型。
    * 
    * @param type
    * @return
    */
   public JQueryOptions useT(JValueType type) {
      this.t = true;
      this.type = type;
      return this;
   }

   /**
    * 详细: 显示值名称类型的数字等值
    * 
    * @return
    */
   public JQueryOptions useZ() {
      this.z = true;
      return this;
   }

   /**
    * 为 REG_MULTI_SZ 在数据字符串中指定分隔符(长度只为 1 个字符)。 默认分隔符为 "\0"
    * 
    * @param separator
    * @return
    */
   public JQueryOptions useSE(Character separator) {
      this.separator = separator;
      this.se = true;
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

      if (this.f && this.data != null && this.data.length() > 0) {
         if (builder.length() > 0) {
            builder.append(" ");
         }
         builder.append("/f ").append(this.data);
         if (this.k) {
            builder.append(" /k");
         }
         if (this.d) {
            builder.append(" /d");
         }
         if (this.c) {
            builder.append(" /c");
         }
         if (this.e) {
            builder.append(" /e");
         }
      }

      if (this.s) {
         if (builder.length() > 0) {
            builder.append(" ");
         }
         builder.append("/s");
      }

      if (this.t && this.type != null) {
         if (builder.length() > 0) {
            builder.append(" ");
         }
         return "/t " + type.name();
      }

      if (this.z) {
         if (builder.length() > 0) {
            builder.append(" ");
         }
         builder.append("/z");
      }

      if (this.se && this.separator != null) {
         if (builder.length() > 0) {
            builder.append(" ");
         }
         return "/se " + separator;
      }
      return builder.toString();
   }

}
