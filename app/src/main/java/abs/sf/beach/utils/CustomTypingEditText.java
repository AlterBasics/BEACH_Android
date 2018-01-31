package abs.sf.beach.utils;


import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

public class CustomTypingEditText extends EditText implements TextWatcher {
    private static final int TypingInterval = 5000;

    public interface OnTypingModified {
        void onIsTypingModified(EditText view, boolean isTyping);
    }

    private OnTypingModified typingChangedListener;

    private boolean currentTypingState = false;
    private Handler handler = new Handler();
    private Runnable stoppedTypingNotifier = new Runnable() {
        @Override
        public void run() {
            if (null != typingChangedListener) {
                typingChangedListener.onIsTypingModified(CustomTypingEditText.this, false);
                currentTypingState = false;
            }
        }
    };

    public CustomTypingEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.addTextChangedListener(this);

    }


    public CustomTypingEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.addTextChangedListener(this);

    }

    public CustomTypingEditText(Context context) {
        super(context);
        this.addTextChangedListener(this);
    }

    public void setOnTypingModified(OnTypingModified typingChangedListener) {
        this.typingChangedListener = typingChangedListener;
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (null != typingChangedListener) {
            if (!currentTypingState) {
                typingChangedListener.onIsTypingModified(this, true);
                currentTypingState = true;
            }

            handler.removeCallbacks(stoppedTypingNotifier);
            handler.postDelayed(stoppedTypingNotifier, TypingInterval);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }


    @Override
    public void onTextChanged(CharSequence text, int start, int before, int after) {
    }

}
