/*
 * Copyright (C) 2012 Google Inc.
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

package com.google.gson.interceptors;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

/**
 * A factory for creating type adapters that intercept JSON serialization and deserialization.
 * <p>
 * This class implements the {@code TypeAdapterFactory} interface, which is used by Gson to create
 * type adapters for specific types. It checks whether the type being serialized or deserialized
 * has an {@code @Intercept} annotation and, if so, returns a new type adapter that applies the
 * post-deserialization method specified in the annotation.
 */
public final class InterceptorFactory implements TypeAdapterFactory {


  /**
   * Creates a new type adapter for the given type, if it has an {@code @Intercept} annotation.
   * <p>
   * This method checks whether the given type has an {@code @Intercept} annotation and, if so,
   * returns a new type adapter that delegates to the default type adapter for the type, but also
   * applies the post-deserialization method specified in the annotation.
   *
   * @param gson the Gson instance that is creating the type adapter
   * @param type the type being serialized or deserialized
   * @return a new type adapter for the type, or {@code null} if the type has no {@code @Intercept}
   *         annotation
   */
  @Override public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
    Intercept intercept = type.getRawType().getAnnotation(Intercept.class);
    if (intercept == null) {
      return null;
    }

    TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
    return new InterceptorAdapter<>(delegate, intercept);
  }

  /**
   * A type adapter that intercepts JSON deserialization by calling a post-deserialization method.
   * <p>
   * This class extends the {@code TypeAdapter} class and overrides the {@code read()} method to
   * first delegate to the default type adapter for the type, and then call the post-deserialization
   * method specified in the {@code @Intercept} annotation.
   */
  static class InterceptorAdapter<T> extends TypeAdapter<T> {
    private final TypeAdapter<T> delegate;
    private final JsonPostDeserializer<T> postDeserializer;


    /**
     * Creates a new instance of the type adapter with the given delegate and post-deserializer.
     * <p>
     * This constructor initializes the delegate type adapter and the post-deserializer with the
     * given arguments.
     *
     * @param delegate the default type adapter for the type being serialized or deserialized
     * @param intercept the {@code @Intercept} annotation on the type being serialized or deserialized
     */
    @SuppressWarnings("unchecked") // ?
    public InterceptorAdapter(TypeAdapter<T> delegate, Intercept intercept) {
      try {
        this.delegate = delegate;
        this.postDeserializer = intercept.postDeserialize().getDeclaredConstructor().newInstance();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    /**
     * Serializes the given value to a JSON output stream.
     * <p>
     * This method delegates to the default type adapter for the type, and does not modify the output.
     *
     * @param out the JSON output stream to write to
     * @param value the value to serialize
     * @throws IOException if an error occurs while writing to the output stream
     */
    @Override public void write(JsonWriter out, T value) throws IOException {
      delegate.write(out, value);
    }

    @Override public T read(JsonReader in) throws IOException {
      T result = delegate.read(in);
      postDeserializer.postDeserialize(result);
      return result;
    }
  }
}
