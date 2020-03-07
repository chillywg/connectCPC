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
 * 执行结果
 * 
 * @author jianggujin
 *
 */
public class JExecResult {
   private final int exitValue;
   private boolean success;
   private final String[] lines;

   public JExecResult(int exitValue, String[] lines) {
      this.exitValue = exitValue;
      this.success = exitValue == 0;
      this.lines = lines;
   }

   public int getExitValue() {
      return exitValue;
   }

   public boolean isSuccess() {
      return success;
   }

   public String[] getLines() {
      return lines;
   }

   protected void setSuccess(boolean success) {
      this.success = success;
   }
}
