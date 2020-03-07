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
 * 删除选项
 * 
 * @author jianggujin
 *
 */
public class JDeleteOptions implements JOptions {
   private boolean v;
   private String valueName;

   private boolean ve;
   private boolean va;

   private boolean f;

   /**
    * 所选项之下要删除的值的名称。省略时，该项下的所有子项的值都会被删除
    * 
    * @param valueName
    */
   public JDeleteOptions useV(String valueName) {
      this.v = true;
      this.valueName = valueName;
      this.ve = false;
      this.va = false;
      return this;
   }

   /**
    * 删除空值名称的值(默认)
    * 
    * @return
    */
   public JDeleteOptions useVE() {
      this.ve = true;
      this.v = false;
      this.va = false;
      return this;
   }

   /**
    * 删除该项下面的所有值
    * 
    * @return
    */
   public JDeleteOptions useVA() {
      this.va = true;
      this.v = false;
      this.ve = false;
      return this;
   }

   /**
    * 不用提示，强制删除
    * 
    * @return
    */
   public JDeleteOptions useF() {
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

      if (this.va) {
         if (builder.length() > 0) {
            builder.append(" ");
         }
         builder.append("/va");
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
