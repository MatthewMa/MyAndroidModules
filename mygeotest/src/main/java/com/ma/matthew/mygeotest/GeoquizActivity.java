package com.ma.matthew.mygeotest;

import android.content.Context;
import android.content.Intent;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ma.matthew.mygeotest.model.Question;

public class GeoquizActivity extends AppCompatActivity {
    private Button mTrueBtn;
    private Button mFalseBtn;
    private ImageButton mNextButton;
    private ImageButton mPreviousButton;
    private TextView mQuestionTextView;
    private Button mCheatButton;
    private int score = 0;
    private static final String KEY_INDEX = "index";
    private static final String TAG = "GeoquizActivity";
    private static final String EXTRA_ANSWER_IS_TRUE =
            "com.ma.matthew.mygeotest.answer_is_true";
    private static final String EXTRA_ANSWER_SHOWN =
            "com.ma.matthew.mygeotest.answer_shown";

    private static final int REQUEST_CODE_CHEAT = 0;
    private Question[] mQuestionBank = new Question[] {
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };
    private boolean[] mIsCheater = new boolean[mQuestionBank.length];
    private int mCurrentIndex = 0;

    /**
     *  Return a cheat activity intent
     * @return
     */
    private static Intent newCheatIntent(Context context, boolean isAnswerTrue){
        Intent intent = new Intent(context,CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE,isAnswerTrue);
        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geoquiz);
        Log.d(TAG,"The activity started!");
        mTrueBtn = findViewById(R.id.true_button);
        mFalseBtn = findViewById(R.id.false_button);
        mNextButton = findViewById(R.id.next_button);
        mPreviousButton = findViewById(R.id.previous_button);
        mQuestionTextView = findViewById(R.id.question_text_view);
        mCheatButton = findViewById(R.id.cheat_button);
        if(savedInstanceState != null){
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX);
            mIsCheater = savedInstanceState.getBooleanArray(EXTRA_ANSWER_SHOWN);
        }
        int currentQuestionSourceId = mQuestionBank[mCurrentIndex].getTextResId();
        setPreviousNextButton();
        mQuestionTextView.setText(currentQuestionSourceId);
        mTrueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(true);
            }
        });
        mFalseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               checkAnswer(false);
            }
        });
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex ++;
                setPreviousNextButton();
                updateQuestion();
            }
        });

        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex --;
                setPreviousNextButton();
                updateQuestion();
            }
        });
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start cheat activity
                boolean isAnswerTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                startActivityForResult(newCheatIntent(GeoquizActivity.this,isAnswerTrue),REQUEST_CODE_CHEAT);
            }
        });
    }

    private void setPreviousNextButton() {
        mPreviousButton.setVisibility(View.VISIBLE);
        mNextButton.setVisibility(View.VISIBLE);
        if(mCurrentIndex == 0) {
            mPreviousButton.setVisibility(View.INVISIBLE);
        }
        if(mCurrentIndex == mQuestionBank.length - 1) {
            mNextButton.setVisibility(View.INVISIBLE);
        }
    }

    private void updateQuestion() {
        int questionId = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(questionId);
        // Enable true and false buttons
        mTrueBtn.setEnabled(true);
        mFalseBtn.setEnabled(true);
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageResId = 0;
        if(mIsCheater[mCurrentIndex]){
            messageResId = R.string.judgment_toast;
        } else{
            if (userPressedTrue == answerIsTrue) {
                messageResId = R.string.correct_toast;
                // Add score
                score += (100/mQuestionBank.length);
            } else {
                messageResId = R.string.incorrect_toast;
            }
        }
        // Disable true and false buttons
        mTrueBtn.setEnabled(false);
        mFalseBtn.setEnabled(false);
        if(mCurrentIndex == mQuestionBank.length - 1){
            Toast.makeText(GeoquizActivity.this,String.format("Your total score is %d", score),Toast.LENGTH_LONG).show();
        } else{
            Toast.makeText(this, messageResId, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG,"Saved instance start!");
        outState.putInt(KEY_INDEX,mCurrentIndex);
        outState.putBooleanArray(EXTRA_ANSWER_SHOWN,mIsCheater);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode != RESULT_OK){
            return;
        }
        if(requestCode == REQUEST_CODE_CHEAT){
            if(data == null){
                return;
            }
            mIsCheater[mCurrentIndex] = CheatActivity.wasAnswerShown(data);
        }
    }
}
