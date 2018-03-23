package com.example.craig.triviafinal;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout questionContainer = (LinearLayout) findViewById(R.id.questionContainer);
        TriviaCollection triviaCollection = new TriviaCollection(this, questionContainer);
        triviaCollection.newMultipleChoiceQuestion(getString(R.string.questionOne), new String[]{"5", "1", "3", "6"});
        triviaCollection.newMultipleChoiceQuestion(getString(R.string.questionTwo), new String[]{getString(R.string.answer2_1), getString(R.string.answer2_2), getString(R.string.answer2_3), getString(R.string.answer2_4)});
        triviaCollection.newMultipleChoiceQuestion(getString(R.string.questionThree), new String[]{getString(R.string.answer3_1), getString(R.string.answer3_2), getString(R.string.answer3_3), getString(R.string.answer3_4), getString(R.string.answer3_5)});
        triviaCollection.newMultipleChoiceQuestion(getString(R.string.questionFour), new String[]{getString(R.string.answer4_1), getString(R.string.answer4_2), getString(R.string.answer4_3), getString(R.string.answer4_4)});
        triviaCollection.newMultipleChoiceQuestion(getString(R.string.questionFive), new String[]{getString(R.string.answer5_1), getString(R.string.answer5_2), getString(R.string.answer5_3), getString(R.string.answer5_4)});
        triviaCollection.newMultipleChoiceQuestion(getString(R.string.questionSix), new String[]{getString(R.string.answer6_1), getString(R.string.answer6_2), getString(R.string.answer6_3), getString(R.string.answer6_4)});
        triviaCollection.newOpenEndedQuestion("Where were the first Magic expansions based?", "Dominaria");
        triviaCollection.newCheckBoxQuestion("What planes have been visited in a Magic expansion.", new String[]{"Dominaria", "Kaladesh", "Ixalan"}, new String[]{"Xylotl", "Hyrule"});
        triviaCollection.display();
    }
}
