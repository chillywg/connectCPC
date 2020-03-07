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
 * 复制选项
 * 
 * @author jianggujin
 *
 */
public class JCopyOptions implements JOptions {
   private boolean s;
   private boolean f;

   /**
    * 复制所有子项和值
    * 
    * @return
    */
   public JCopyOptions useS() {
      this.s = true;
      return this;
   }

   /**
    * 不用提示，强制复制
    * 
    * @return
    */
   public JCopyOptions useF() {
      this.f = true;
      return this;
   }

   @Override
   public String toOptions() {
      StringBuilder builder = new StringBuilder();
      if (this.s) {
         builder.append("/s");
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
