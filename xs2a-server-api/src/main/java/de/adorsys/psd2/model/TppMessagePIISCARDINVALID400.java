/*
 * Copyright 2018-2018 adorsys GmbH & Co KG
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

package de.adorsys.psd2.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Addressed card number is unknown to the ASPSP or not associated to the PSU.
 */
@ApiModel(description = "Addressed card number is unknown to the ASPSP or not associated to the PSU. ")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2018-08-06T12:02:27.187+02:00[Europe/Berlin]")
public class TppMessagePIISCARDINVALID400 {

    @JsonProperty("category")
    private TppMessageCategory category = null;
    @JsonProperty("code")
    private CodeEnum code = null;
    @JsonProperty("path")
    private String path = null;
    @JsonProperty("text")
    private String text = null;

    public TppMessagePIISCARDINVALID400 category(TppMessageCategory category) {
        this.category = category;
        return this;
    }

    /**
     * Get category
     *
     * @return category
     **/
    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Valid
    public TppMessageCategory getCategory() {
        return category;
    }

    public void setCategory(TppMessageCategory category) {
        this.category = category;
    }

    public TppMessagePIISCARDINVALID400 code(CodeEnum code) {
        this.code = code;
        return this;
    }

    /**
     * Get code
     *
     * @return code
     **/
    @ApiModelProperty(required = true, value = "")
    @NotNull

    public CodeEnum getCode() {
        return code;
    }

    public void setCode(CodeEnum code) {
        this.code = code;
    }

    public TppMessagePIISCARDINVALID400 path(String path) {
        this.path = path;
        return this;
    }

    /**
     * Get path
     *
     * @return path
     **/
    @ApiModelProperty(value = "")
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public TppMessagePIISCARDINVALID400 text(String text) {
        this.text = text;
        return this;
    }

    /**
     * Get text
     *
     * @return text
     **/
    @ApiModelProperty(value = "")
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TppMessagePIISCARDINVALID400 tppMessagePIISCARDINVALID400 = (TppMessagePIISCARDINVALID400) o;
        return Objects.equals(this.category, tppMessagePIISCARDINVALID400.category)
            && Objects.equals(this.code, tppMessagePIISCARDINVALID400.code)
            && Objects.equals(this.path, tppMessagePIISCARDINVALID400.path)
            && Objects.equals(this.text, tppMessagePIISCARDINVALID400.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(category, code, path, text);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class TppMessagePIISCARDINVALID400 {\n");

        sb.append("    category: ").append(toIndentedString(category)).append("\n");
        sb.append("    code: ").append(toIndentedString(code)).append("\n");
        sb.append("    path: ").append(toIndentedString(path)).append("\n");
        sb.append("    text: ").append(toIndentedString(text)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

    /**
     * Gets or Sets code
     */
    public enum CodeEnum {
        INVALID("CARD_INVALID");

        private String value;

        CodeEnum(String value) {
            this.value = value;
        }

        @JsonCreator
        public static CodeEnum fromValue(String text) {
            for (CodeEnum b : CodeEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }
    }
}
