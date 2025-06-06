package com.syalux.quizapp.utilities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.syalux.quizapp.R;
import com.syalux.quizapp.models.Exam;

import java.util.List;

public class ExamManagementAdapter extends RecyclerView.Adapter<ExamManagementAdapter.ExamViewHolder> {

    private final List<Exam> exams;
    private final OnExamActionListener listener;

    public ExamManagementAdapter(List<Exam> exams, OnExamActionListener listener) {
        this.exams = exams;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ExamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exam_management, parent, false);
        return new ExamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExamViewHolder holder, int position) {
        Exam exam = exams.get(position);
        holder.tvExamName.setText(exam.getExamName());
        holder.tvQuestionCount.setText("Questions: " + exam.getNumberOfQuestions());
        holder.tvPublishStatus.setText("Status: " + (exam.isPublished() ? "Published" : "Unpublished"));
        holder.btnTogglePublish.setText(exam.isPublished() ? "Unpublish" : "Publish");

        holder.btnEditExam.setOnClickListener(v -> listener.onEditExam(exam));
        holder.btnManageQuestions.setOnClickListener(v -> listener.onManageQuestions(exam));
        holder.btnTogglePublish.setOnClickListener(v -> listener.onTogglePublish(exam));
    }

    @Override
    public int getItemCount() {
        return exams.size();
    }

    public static class ExamViewHolder extends RecyclerView.ViewHolder {
        TextView tvExamName, tvQuestionCount, tvPublishStatus;
        Button btnEditExam, btnManageQuestions, btnTogglePublish;

        public ExamViewHolder(@NonNull View itemView) {
            super(itemView);
            tvExamName = itemView.findViewById(R.id.tvExamName);
            tvQuestionCount = itemView.findViewById(R.id.tvQuestionCount);
            tvPublishStatus = itemView.findViewById(R.id.tvPublishStatus);
            btnEditExam = itemView.findViewById(R.id.btnEditExam);
            btnManageQuestions = itemView.findViewById(R.id.btnManageQuestions);
            btnTogglePublish = itemView.findViewById(R.id.btnTogglePublish);
        }
    }

    public interface OnExamActionListener {
        void onEditExam(Exam exam);
        void onManageQuestions(Exam exam);
        void onTogglePublish(Exam exam);
    }
}