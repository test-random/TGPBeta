package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.messenger.MessagesController;

public final class MessagesStorage$$ExternalSyntheticLambda194 implements Comparator {
    public static final MessagesStorage$$ExternalSyntheticLambda194 INSTANCE = new MessagesStorage$$ExternalSyntheticLambda194();

    private MessagesStorage$$ExternalSyntheticLambda194() {
    }

    @Override
    public final int compare(Object obj, Object obj2) {
        int lambda$loadDialogFilters$40;
        lambda$loadDialogFilters$40 = MessagesStorage.lambda$loadDialogFilters$40((MessagesController.DialogFilter) obj, (MessagesController.DialogFilter) obj2);
        return lambda$loadDialogFilters$40;
    }
}
