package com.example.craig.triviafinal;

import android.content.Context;
import android.graphics.Color;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
                Toast.makeText(mContext, getResults(), Toast.LENGTH_LONG).show();
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
     * Method to build multiple choice questions
     * @param question String for the question
     * @param answers Array holding question answers. First index should contain the correct answer.
     */
    void newMultipleChoiceQuestion(String question, String[] answers){
        MultipleChoiceQuestion temp = new MultipleChoiceQuestion(mContext, question);
        Random r = new Random();
        int answerOffset = Math.abs(r.nextInt())%answers.length;
        for (int i = 0; i < answers.length; i++){
            int idx = (i + answerOffset)%answers.length;
            temp.addAnswer(answers[idx], idx == 0);
        }
        mQuestions.add(temp);
    }

    void newOpenEndedQuestion(String question, String answer){
        OpenEndedQuestion temp = new OpenEndedQuestion(mContext, question);
        temp.setAnswer(answer);
        mQuestions.add(temp);
    }

    void newCheckBoxQuestion(String question, String[] correctAnswers, String[] incorrectAnswers){
        CheckBoxQuestion temp = new CheckBoxQuestion(mContext, question);
        temp.addCorrectAnswers(correctAnswers);
        temp.addIncorrectAnswers(incorrectAnswers);
        mQuestions.add(temp);
    }

    /**
     * Calculate quiz results.
     * @return Returns a string describing test results in the format of correct/total.
     */
    private String getResults(){
        int numCorrect = 0;
        for (TriviaQuestion question : mQuestions){
            question.disableInputs();
            if (question.checkAnswer())
                numCorrect++;

        }
        return format(Locale.ENGLISH, mContext.getString(R.string.scoreText), numCorrect, mQuestions.size());
    }
}

abstract class TriviaQuestion{
    protected LinearLayout mLayout;
    protected TextView mQuestionView;
    protected TextView mReportView;
    protected Context mContext;

    TriviaQuestion(Context context, String question){
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

        mReportView = new TextView(context);

        mQuestionView = new TextView(context);
        mQuestionView.setText(question);
        mQuestionView.setTextColor(Color.WHITE);
    }

    abstract LinearLayout asLayout();
    abstract void disableInputs();
    abstract boolean checkAnswer();
}

class OpenEndedQuestion extends TriviaQuestion{
    EditText mTextInput;
    private String mCorrectAnswer;

    OpenEndedQuestion(Context context, String question){
        super(context, question);
        mTextInput = new EditText(mContext);
        mTextInput.setHint("Enter your response here!");
        mTextInput.setInputType(InputType.TYPE_CLASS_TEXT);
    }

    void setAnswer(String answer){
        mCorrectAnswer = answer;
    }

    @Override
    LinearLayout asLayout(){
        mLayout.addView(mQuestionView);
        mLayout.addView(mTextInput);
        mLayout.addView(mReportView);
        return mLayout;
    }

    @Override
    boolean checkAnswer(){
        boolean result = mTextInput.getText().toString().equals(mCorrectAnswer);
        mReportView.setText(String.format(mContext.getString(R.string.isCorrectTxt), (result ? mContext.getString(R.string.correctTxt) : mContext.getString(R.string.incorrectTxt))));
        return (result);
    }

    @Override
    void disableInputs(){
        mTextInput.setEnabled(false);
    }
}
class MultipleChoiceQuestion extends TriviaQuestion {
    RadioGroup mAnswers;
    private int mCorrectAnswer;

    MultipleChoiceQuestion(Context context, String question) {
        super(context, question);
        mAnswers = new RadioGroup(context);
    }

    void addAnswer(String answer, Boolean isCorrect) {
        RadioButton btn = new RadioButton(mContext);
        btn.setText(answer);
        mAnswers.addView(btn);
        if (isCorrect) {
            mCorrectAnswer = btn.getId();
        }
    }

    @Override
    boolean checkAnswer() {
        int checkedId = mAnswers.getCheckedRadioButtonId();
        boolean result = checkedId == mCorrectAnswer;
        mReportView.setText(String.format(mContext.getString(R.string.isCorrectTxt), (result ? mContext.getString(R.string.correctTxt) : mContext.getString(R.string.incorrectTxt))));
        return (result);
    }

    @Override
    LinearLayout asLayout() {
        mLayout.addView(mQuestionView);
        mLayout.addView(mAnswers);
        mLayout.addView(mReportView);
        return mLayout;
    }

    @Override
    void disableInputs(){
        for(int i = 0; i < mAnswers.getChildCount(); i++)
            mAnswers.getChildAt(i).setEnabled(false); //Disable radio buttons after test submitted.
    }
}
class CheckBoxQuestion extends TriviaQuestion{
    ArrayList<CheckBox> mCorrectAnswers;
    ArrayList<CheckBox> mIncorrectAnswers;

    CheckBoxQuestion(Context context, String question){
        super(context, question);
        mCorrectAnswers = new ArrayList<>();
        mIncorrectAnswers = new ArrayList<>();
    }

    void addCorrectAnswers(String[] answers){
        for (String answer : answers){
            CheckBox temp = new CheckBox(mContext);
            temp.setText(answer);
            mCorrectAnswers.add(temp);
        }
    }

    void addIncorrectAnswers(String[] answers){
        for (String answer : answers){
            CheckBox temp = new CheckBox(mContext);
            temp.setText(answer);
            mIncorrectAnswers.add(temp);
        }
    }

    @Override
    LinearLayout asLayout(){
        mLayout.addView(mQuestionView);

        Random r = new Random();
        ArrayList<CheckBox> allAnswers = new ArrayList<>();
        allAnswers.addAll(mCorrectAnswers);
        allAnswers.addAll(mIncorrectAnswers);

        while(allAnswers.size() > 0){
            int chosen = Math.abs(r.nextInt())%allAnswers.size();
            mLayout.addView(allAnswers.get(chosen));
            allAnswers.remove(chosen);
        }

        mLayout.addView(mReportView);
        return mLayout;
    }

    @Override
    boolean checkAnswer() {
        boolean isCorrect = true;
        for (CheckBox answer:mCorrectAnswers)
            isCorrect = isCorrect && (answer.isChecked());
        for (CheckBox answer:mIncorrectAnswers)
            isCorrect = isCorrect && !(answer.isChecked());
        mReportView.setText(String.format(mContext.getString(R.string.isCorrectTxt), (isCorrect ? mContext.getString(R.string.correctTxt) : mContext.getString(R.string.incorrectTxt))));
        return isCorrect;
    }

    @Override
    void disableInputs() {
        for (CheckBox answer:mCorrectAnswers)
            answer.setEnabled(false);
        for (CheckBox answer:mIncorrectAnswers)
            answer.setEnabled(false);
    }


}
