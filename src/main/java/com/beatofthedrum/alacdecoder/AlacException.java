package com.beatofthedrum.alacdecoder;

import java.io.IOException;

public class AlacException extends IOException {
    private static final long serialVersionUID = -3663423055479420454L;
    public AlacException(String message) {
        super(message);
    }
    public AlacException(String message, Throwable cause) {
        super(message, cause);
    }
    public AlacException(Throwable cause) {
        super(cause);
    }
    public AlacException() {
    }
}
