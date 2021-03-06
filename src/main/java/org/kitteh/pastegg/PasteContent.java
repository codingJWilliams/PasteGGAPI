/*
 * * Copyright (C) 2018 Matt Baxter https://kitteh.org
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.kitteh.pastegg;

import com.google.gson.annotations.SerializedName;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.function.Function;
import java.util.zip.GZIPOutputStream;

public class PasteContent {
    public enum ContentType {
        @SerializedName("base64")
        BASE64(string -> Base64.getUrlEncoder().encodeToString(string.getBytes())),
        @SerializedName("gzip")
        GZIP(string -> {
            byte[] bytes = string.getBytes();

            try {
                ByteArrayOutputStream byteOutput = new ByteArrayOutputStream(bytes.length);
                try {
                    GZIPOutputStream gzipOutput = new GZIPOutputStream(byteOutput);
                    try {
                        gzipOutput.write(bytes);
                    } finally {
                        gzipOutput.close();
                    }
                } finally {
                    byteOutput.close();
                }
                return Base64.getUrlEncoder().encodeToString(byteOutput.toByteArray());
            } catch (Exception e) {
                throw new RuntimeException(); // TODO
            }
        }
        ),
        @SerializedName("text")
        TEXT(string -> string),
        @SerializedName("xz")
        XZ(string -> string); // TODO

        private final Function<String, String> processor;

        private ContentType(Function<String, String> processor) {
            this.processor = processor;
        }

        public Function<String, String> getProcessor() {
            return this.processor;
        }
    }

    private ContentType format;
    private String value;

    private transient String processedValue;

    public PasteContent(ContentType format, String value) {
        this.format = format;
        this.value = format.getProcessor().apply(value);
        this.processedValue = value;
    }

    public String getValue() {
        if (this.processedValue == null) {
            // TODO magic
        }
        return this.processedValue;
    }
}
