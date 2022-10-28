package com.example.imageslider;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imageslider.Ultis.ImageUtils;

import org.w3c.dom.Text;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private List<File> ListImages;


    public interface OnItemClickListener
    {
        void onClick(View view, int position);
    }

    private OnItemClickListener listener;

    public void setListener(OnItemClickListener listener)
    {
        this.listener = listener;
    }

    public ImageAdapter(List<File> ListImages)
    {
        this.ListImages = ListImages;
    }

    public void setListImages(List<File> list) {this.ListImages = list;}

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.list_images_row, null);
        return new ViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.Bind(ListImages.get(position));
    }

    @Override
    public int getItemCount() {
        return ListImages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public ImageView imageView;
        public TextView imageName;
        private Context context;

        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            imageView = itemView.findViewById(R.id.imageView);
            imageName = itemView.findViewById(R.id.imgName);
            itemView.setOnClickListener(this);
        }


        public void Bind(File image)
        {
            ImageUtils.from(context).load(image.getAbsolutePath()).apply(imageView);
            imageName.setText(image.getName());
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v, getAdapterPosition());
        }
    }

}
