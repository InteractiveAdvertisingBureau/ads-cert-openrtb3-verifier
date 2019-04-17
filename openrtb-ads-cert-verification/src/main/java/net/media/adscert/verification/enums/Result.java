/*
 * Copyright © 2019 - present. MEDIA NET SOFTWARE SERVICES PVT. LTD.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.media.adscert.verification.enums;

/**
 * Stores the output of a verification.
 *
 * @author anupam verma
 * @since 1.0
 */
public class Result {

  private Status status;
  private String message;
  private Exception exception;

  public Result(Status status, String message) {
    this.status = status;
    this.message = message;
  }

  public Result(Status status, String message, Exception exception) {
    this.status = status;
    this.message = message;
    this.exception = exception;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Exception getException() {
    return exception;
  }

  public void setException(Exception exception) {
    this.exception = exception;
  }

  public enum Status {
    SUCCESS,
    FAILURE,
    SAMPLED
  }
}
