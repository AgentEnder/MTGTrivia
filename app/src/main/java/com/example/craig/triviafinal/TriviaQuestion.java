package com.example.craig.triviafinal;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static java.lang.String.format;


class TriviaCollection {
    private ArrayList<TriviaQuestion> mQuestions; //A container to hold the questions
    private Context mContext; //Store Context so that it can be referred to throughout class.
    private LinearLayout mContainer; //Store container for access in the display method.
    private Button mSubmitBtn; //Store button for onClick listen.

    /**
     * Constructor
     * @param context Context, usually should be "this"
     * @param display A linear layout in which to display the quiz.
     */
    TriviaCollection(final Context context, LinearLayout display) {
        mContext = context; //Store context
        mContainer = display; //Store display
        mQuestions = new ArrayList<>(); //Init list for questions
        mSubmitBtn = new Button(context); //Init submit button
        mSubmitBtn.setText(R.string.SubmitTxt);
        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, getResults(), 2).show();
            }
        });
    }

    /**
     *Add a question that has already been created to the lists of questions.
     * @param question The TriviaQuestion object to be added.
     */
    void addQuestion(TriviaQuestion question) {
        mQuestions.add(question);
    }

    /**
     * Populate the LinearLayout with questions and a submit btn.
     */
    void display() {
        mContainer.removeAllViewsInLayout(); //Clear the layout of all current children.
        for (TriviaQuestion question : mQuestions) {
            mContainer.addView(question.asLayout()); //Add each question
        }
        mContainer.addView(mSubmitBtn); //Add submit btn.
    }

    /**
     * Method to build questions
     * @param question String for the question
     * @param answers Array holding question answers. First index should contain the correct answer.
     */
    void newQuestion(String question, String[] answers){
        TriviaQuestion temp = new TriviaQuestion(mContext, question);
        Random r = new Random();
        int answerOffset = Math.abs(r.nextInt())%answers.length;
        for (int i = 0; i < answers.length; i++){
            int idx = (i + answerOffset)%answers.length;
            temp.addAnswer(answers[idx], idx == 0);
        }
        mQuestions.add(temp);
    }

    /**
     * Calculate quiz results.
     * @return Returns a string describing test results in the format of correct/total.
     */
    private String getResults(){
        int numCorrect = 0;
        for (TriviaQuestion question : mQuestions){
            for(int i = 0; i < question.mAnswers.getChildCount(); i++)
                question.mAnswers.getChildAt(i).setEnabled(false); //Disable radio buttons after test submitted.
            if (question.checkAnswer())
                numCorrect++;
        }
        return format(Locale.ENGLISH, mContext.getString(R.string.scoreText), numCorrect, mQuestions.size());
    }
}

class TriviaQuestion {
    RadioGroup mAnswers;
    private LinearLayout mLayout;
    private int mCorrectAnswer;
    private TextView mQuestionView;
    private TextView mReportView;
    private Context mContext;

    TriviaQuestion(Context context, String question) {
        mContext = context;

        mLayout = new LinearLayout(context);
        float scale = mContext.getResources().getDisplayMetrics().density;
        int padding = (int) (16 * scale + 0.5f);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        layoutParams.setMargins(padding, padding, padding, padding);
        mLayout.setLayoutParams(layoutParams);
        mLayout.setPadding(padding, padding, padding, padding);
        mLayout.setOrientation(LinearLayout.VERTICAL);
        mLayout.setBackgroundResource(R.drawable.question_background);

        mAnswers = new RadioGroup(context);

        mReportView = new TextView(context);

        mQuestionView = new TextView(context);
        mQuestionView.setText(question);
        mQuestionView.setTextColor(Color.WHITE);

    }

    void addAnswer(String answer, Boolean isCorrect) {
        RadioButton btn = new RadioButton(mContext);
        btn.setText(answer);
        mAnswers.addView(btn);
        if (isCorrect) {
            mCorrectAnswer = btn.getId();
        }
    }

    Boolean checkAnswer() {
        int checkedId = mAnswers.getCheckedRadioButtonId();
        Boolean result = checkedId == mCorrectAnswer;
        mReportView.setText(String.format(mContext.getString(R.string.isCorrectTxt), (result ? mContext.getString(R.string.correctTxt) : mContext.getString(R.string.incorrectTxt))));
        return (result);
    }

    LinearLayout asLayout() {
        mLayout.addView(mQuestionView);
        mLayout.addView(mAnswers);
        mLayout.addView(mReportView);
        return mLayout;
    }
}
