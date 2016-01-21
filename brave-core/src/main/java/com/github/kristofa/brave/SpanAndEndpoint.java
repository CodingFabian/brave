package com.github.kristofa.brave;

import com.github.kristofa.brave.internal.Nullable;
import com.google.auto.value.AutoValue;

import zipkin.Endpoint;
import zipkin.Span;


public interface SpanAndEndpoint {

    /**
     * Gets the span to which to add annotations.
     *
     * @return Span to which to add annotations. Can be <code>null</code>. In that case the different submit methods will not
     *         do anything.
     */
    @Nullable
    Span span();

    /**
     * Gets the Endpoint for the annotations. (Server Endpoint or Client Endpoint depending on the context)
     *
     * @return Endpoint for the annotations. Can be <code>null</code>.
     */
    @Nullable
    Endpoint endpoint();

    /**
     * Span and endpoint never change reference.
     */
    @AutoValue
    abstract class StaticSpanAndEndpoint implements SpanAndEndpoint {
      static StaticSpanAndEndpoint create(@Nullable Span span, @Nullable Endpoint endpoint) {
        return new AutoValue_SpanAndEndpoint_StaticSpanAndEndpoint(span, endpoint);
      }
    }

    @AutoValue
    abstract class ServerSpanAndEndpoint implements SpanAndEndpoint {
        abstract ServerSpanState state();

        static ServerSpanAndEndpoint create(ServerSpanState state) {
            return new AutoValue_SpanAndEndpoint_ServerSpanAndEndpoint(state);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Span span() {
            return state().getCurrentServerSpan().getSpan();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Endpoint endpoint() {
            return state().getServerEndpoint();
        }
    }

    @AutoValue
    abstract class ClientSpanAndEndpoint implements SpanAndEndpoint {
        abstract ServerClientAndLocalSpanState state();

        static ClientSpanAndEndpoint create(ServerClientAndLocalSpanState state) {
            return new AutoValue_SpanAndEndpoint_ClientSpanAndEndpoint(state);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Span span() {
            return state().getCurrentClientSpan();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Endpoint endpoint() {
            return state().getClientEndpoint();
        }
    }

    @AutoValue
    abstract class LocalSpanAndEndpoint implements SpanAndEndpoint {
        abstract ServerClientAndLocalSpanState state();

        static LocalSpanAndEndpoint create(ServerClientAndLocalSpanState state) {
            return new AutoValue_SpanAndEndpoint_LocalSpanAndEndpoint(state);
        }

        @Override
        public Span span() {
            return state().getCurrentLocalSpan();
        }

        /** The local endpoint is the same as the client endpoint. */
        @Override
        public Endpoint endpoint() {
            return state().getClientEndpoint();
        }
    }
}
