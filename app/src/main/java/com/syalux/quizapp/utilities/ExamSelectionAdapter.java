package com.syalux.quizapp.utilities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.syalux.quizapp.R;
import com.syalux.quizapp.models.Exam;

import java.util.List;

public class ExamSelectionAdapter extends RecyclerView.Adapter<ExamSelectionAdapter.ExamViewHolder> {

    private final List<Exam> exams;
    private final OnExamClickListener listener;

    public ExamSelectionAdapter(List<Exam> exams, OnExamClickListener listener) {
        this.exams = exams;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ExamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quiz_category, parent, false);
        return new ExamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExamViewHolder holder, int position) {
        Exam exam = exams.get(position);
        holder.tvCategoryName.setText(exam.getExamName());
        holder.itemView.setOnClickListener(v -> listener.onExamClick(exam));
    }

    @Override
    public int getItemCount() {
        return exams.size();
    }

    public static class ExamViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName;

        public ExamViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
        }
    }

    public interface OnExamClickListener {
        void onExamClick(Exam exam);
    }
}