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
 * 比较选项
 * 
 * @author jianggujin
 *
 */
public class JCompareOptions implements JOptions {
   private boolean v = false;
   private String valueName;

   private boolean ve = false;

   private boolean s = false;
   private boolean oa = false, od = false, os = false, on = false;

   /**
    * 所选注册表项下的要比较的值的名称。 省略时，该项下的所有值都会得到比较
    * 
    * @param valueName
    */
   public JCompareOptions useV(String valueName) {
      this.v = true;
      this.valueName = valueName;
      this.ve = false;
      return this;
   }

   /**
    * 比较空白值名称的值(默认)
    */
   public JCompareOptions useVE() {
      this.ve = true;
      this.v = false;
      return this;
   }

   /**
    * 比较所有子项和值
    */
   public JCompareOptions useS() {
      this.s = true;
      return this;
   }

   /**
    * 显示所有不同和匹配结果
    * 
    * @return
    */
   public JCompareOptions useOA() {
      this.oa = true;
      this.od = false;
      this.os = false;
      this.on = false;
      return this;
   }

   /**
    * 只显示不同的结果
    * 
    * @return
    */
   public JCompareOptions useOD() {
      this.oa = false;
      this.od = true;
      this.os = false;
      this.on = false;
      return this;
   }

   /**
    * 只显示匹配结果
    * 
    * @return
    */
   public JCompareOptions useOS() {
      this.oa = false;
      this.od = false;
      this.os = true;
      this.on = false;
      return this;
   }

   /**
    * 不显示结果
    * 
    * @return
    */
   public JCompareOptions useON() {
      this.oa = false;
      this.od = false;
      this.os = false;
      this.on = true;
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

      if (this.s) {
         if (builder.length() > 0) {
            builder.append(" ");
         }
         builder.append("/s");
      }

      if (this.oa) {
         if (builder.length() > 0) {
            builder.append(" ");
         }
         builder.append("/oa");
      }

      if (this.od) {
         if (builder.length() > 0) {
            builder.append(" ");
         }
         builder.append("/od");
      }

      if (this.os) {
         if (builder.length() > 0) {
            builder.append(" ");
         }
         builder.append("/os");
      }

      if (this.on) {
         if (builder.length() > 0) {
            builder.append(" ");
         }
         builder.append("/on");
      }

      return builder.toString();
   }
}
