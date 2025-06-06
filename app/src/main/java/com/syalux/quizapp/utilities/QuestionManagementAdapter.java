package com.syalux.quizapp.utilities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.syalux.quizapp.R;
import com.syalux.quizapp.models.Question;

import java.util.List;

public class QuestionManagementAdapter extends RecyclerView.Adapter<QuestionManagementAdapter.QuestionViewHolder> {

    private final List<Question> questions;
    private final OnQuestionActionListener listener;

    public QuestionManagementAdapter(List<Question> questions, OnQuestionActionListener listener) {
        this.questions = questions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_question_management, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        Question question = questions.get(position);
        holder.tvQuestionText.setText((position + 1) + ". " + question.getQuestionText());
        holder.tvOptionA.setText("A. " + question.getOptionA());
        holder.tvOptionB.setText("B. " + question.getOptionB());

        if (question.isTrueFalse()) {
            holder.tvOptionC.setVisibility(View.GONE);
            holder.tvOptionD.setVisibility(View.GONE);
        } else {
            holder.tvOptionC.setVisibility(View.VISIBLE);
            holder.tvOptionD.setVisibility(View.VISIBLE);
            holder.tvOptionC.setText("C. " + question.getOptionC());
            holder.tvOptionD.setText("D. " + question.getOptionD());
        }

        String correctAnswerText;
        switch (question.getCorrectAnswer()) {
            case 0: correctAnswerText = "A. " + question.getOptionA(); break;
            case 1: correctAnswerText = "B. " + question.getOptionB(); break;
            case 2: correctAnswerText = "C. " + question.getOptionC(); break;
            case 3: correctAnswerText = "D. " + question.getOptionD(); break;
            default: correctAnswerText = "N/A"; break;
        }
        holder.tvCorrectAnswer.setText("Correct Answer: " + correctAnswerText);

        holder.btnEditQuestion.setOnClickListener(v -> listener.onEditQuestion(question));
        holder.btnDeleteQuestion.setOnClickListener(v -> listener.onDeleteQuestion(question));
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public static class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestionText, tvOptionA, tvOptionB, tvOptionC, tvOptionD, tvCorrectAnswer;
        Button btnEditQuestion, btnDeleteQuestion;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestionText = itemView.findViewById(R.id.tvQuestionText);
            tvOptionA = itemView.findViewById(R.id.tvOptionA);
            tvOptionB = itemView.findViewById(R.id.tvOptionB);
            tvOptionC = itemView.findViewById(R.id.tvOptionC);
            tvOptionD = itemView.findViewById(R.id.tvOptionD);
            tvCorrectAnswer = itemView.findViewById(R.id.tvCorrectAnswer);
            btnEditQuestion = itemView.findViewById(R.id.btnEditQuestion);
            btnDeleteQuestion = itemView.findViewById(R.id.btnDeleteQuestion);
        }
    }

    public interface OnQuestionActionListener {
        void onEditQuestion(Question question);
        void onDeleteQuestion(Question question);
    }
}