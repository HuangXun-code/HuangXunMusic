package edu.whut.huangxun.HuangXunMusic;


import android.content.Context;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class SongAdapter extends ArrayAdapter<Song> {
    private int resourceId;

    public SongAdapter(Context context, int textViewResourceId,
                       List<Song> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Song song = getItem(position); // 获取当前项的Song实例
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.songImage = (ImageView) view.findViewById (R.id.img_cover);
            viewHolder.songName = (TextView) view.findViewById (R.id.txt_song_name);
            TextPaint tp = viewHolder.songName.getPaint();
            tp.setFakeBoldText(true);
            viewHolder.singerName = (TextView) view.findViewById (R.id.txt_singer);
            view.setTag(viewHolder); // 将ViewHolder存储在View中
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag(); // 重新获取ViewHolder
        }
        viewHolder.songImage.setImageResource(song.getImageId());
        viewHolder.songName.setText(song.getName());
        viewHolder.singerName.setText(song.getSingerName());
        viewHolder.songPath = song.getsongPath();
        return view;
    }

    class ViewHolder {
        /**需参考教学资源中课件例子中的“ LIstView的使用”中的FruitAdapter，
         * 定义合适的ViewHolder**/

        ImageView songImage;

        TextView songName;

        TextView singerName;

        String songPath;
    }

}


