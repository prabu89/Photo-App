import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.photoapp.R
import com.photoapp.data.model.Photo
import com.squareup.picasso.Picasso

class PhotosAdapter internal constructor(
    private val context: Context,
    private val photosList: ArrayList<Photo>
) :
    RecyclerView.Adapter<PhotosAdapter.ListViewHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postImageView: ImageView = itemView.findViewById(R.id.flickr_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val itemView = inflater.inflate(R.layout.layout_photo_container, parent, false)
        return ListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val url = "https://live.staticflickr.com/" + photosList[position].server + "/" + photosList[position].id + "_" + photosList[position].secret + ".jpg"
        Picasso.get().load(url).into(holder.postImageView)
    }

    override fun getItemCount(): Int {
        return photosList.size
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }
    fun addItems(photo: ArrayList<Photo>) {
        photosList.addAll(photo)
        notifyDataSetChanged()
    }

    fun clear() {
        photosList.clear()
        notifyDataSetChanged()
    }
}