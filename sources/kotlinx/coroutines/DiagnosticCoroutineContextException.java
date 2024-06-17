package kotlinx.coroutines;

import kotlin.coroutines.CoroutineContext;

final class DiagnosticCoroutineContextException extends RuntimeException {
    private final CoroutineContext context;

    public DiagnosticCoroutineContextException(CoroutineContext coroutineContext) {
        this.context = coroutineContext;
    }

    @Override
    public String getLocalizedMessage() {
        return this.context.toString();
    }

    @Override
    public Throwable fillInStackTrace() {
        setStackTrace(new StackTraceElement[0]);
        return this;
    }
}
