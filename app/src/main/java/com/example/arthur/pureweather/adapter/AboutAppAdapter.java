package com.example.arthur.pureweather.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.arthur.pureweather.R;
import com.example.arthur.pureweather.constant.Constants;
import com.example.arthur.pureweather.utils.MyImageLoader;
import com.example.arthur.pureweather.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/8/8.
 */
public class AboutAppAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private final int INSTRUCTION = 0;
    private final int TECHNOLOGY = 1;
    private final int FEEDBACK = 2;
    private final int DONATION = 3;
    private final int COPY_RIGHT = 4;

    public AboutAppAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                return new InstructionInfo(LayoutInflater.from(context).inflate(R.layout.item_app_instruction, parent, false));
            case 1:
                return new TechnologyInfo(LayoutInflater.from(context).inflate(R.layout.item_app_technology, parent, false));
            case 2:
                return new FeedbackInfo(LayoutInflater.from(context).inflate(R.layout.item_app_feedback, parent, false));
            case 3:
                return new DonationInfo(LayoutInflater.from(context).inflate(R.layout.item_app_donation, parent, false));
            case 4:
                return new CopyRightInfo(LayoutInflater.from(context).inflate(R.layout.item_app_copy_right, parent,false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (position) {
            case INSTRUCTION:
                ((InstructionInfo) holder).bind(context);
                break;
            case TECHNOLOGY:
                ((TechnologyInfo) holder).bind(context);
                break;
            case FEEDBACK:
                ((FeedbackInfo) holder).bind(context);
                break;
            case DONATION:
                ((DonationInfo) holder).bind(context);
                break;
        }

    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return INSTRUCTION;
            case 1:
                return TECHNOLOGY;
            case 2:
                return FEEDBACK;
            case 3:
                return DONATION;
            case 4:
                return COPY_RIGHT;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    class InstructionInfo extends RecyclerView.ViewHolder {
        @Bind(R.id.about_app_instruction_image)
        public ImageView background;

        public InstructionInfo(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(Context context) {
            MyImageLoader.load(context, R.drawable.instruction, background);
        }
    }

    class TechnologyInfo extends RecyclerView.ViewHolder {
        @Bind(R.id.about_app_technology_image)
        public ImageView background;
        @Bind(R.id.app_technology_btn)
        public Button button;

        public TechnologyInfo(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(Context context) {
            MyImageLoader.load(context, R.drawable.technology, background);
            button.setOnClickListener(v -> {
                Uri uri = Uri.parse(Constants.GIT_HUB_ADDRESS);   //指定网址
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);           //指定Action
                intent.setData(uri);                            //设置Uri
                context.startActivity(intent);
            });
        }
    }

    class FeedbackInfo extends RecyclerView.ViewHolder {
        @Bind(R.id.about_app_feedback_image)
        public ImageView background;
        @Bind(R.id.app_feedback_btn)
        public Button button;

        public FeedbackInfo(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(Context context) {
            MyImageLoader.load(context, R.drawable.feedback, background);
            button.setOnClickListener(v -> Utils.copyToClipboard(Constants.EMAIL_ADDRESS, context));
        }
    }

    class DonationInfo extends RecyclerView.ViewHolder {
        @Bind(R.id.about_app_donation_image)
        public ImageView background;
        @Bind(R.id.app_donation_btn)
        public Button button;

        public DonationInfo(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(Context context) {
            MyImageLoader.load(context, R.drawable.donation, background);
            button.setOnClickListener(v -> Utils.copyToClipboard(Constants.DONATION_ADDRESS, context));
        }
    }
    class CopyRightInfo extends RecyclerView.ViewHolder {

        public CopyRightInfo(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
