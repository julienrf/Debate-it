package models;

import org.parsit.IFootNote;
import org.parsit.IFootNoteFactory;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import siena.Id;
import siena.Index;
import siena.Model;
import siena.Query;
import siena.Table;

@Table("footnotes")
public class FootNote extends Model implements IFootNote {
	
	@Id
	public Long id;
	
	/** Footnote HTML content */
	@Required @MaxSize(1000) // Play validator
	public String content;
	
	/** Parent post where this footnote is referenced */
	@Required
	@Index("paragraph_index")
	public Paragraph paragraph;
	
	
	public FootNote() {
		
	}
	
	public static Query<FootNote> all() {
		return Model.all(FootNote.class);
	}
	
	@Override
	public String getIdentifier() {
		return id.toString() + "_";
	}
	
	@Override
	public String getContent() {
		return content;
	}

	@Override
	public void setContent(String content) {
		this.content = content;
	}
	
	
	public static class Factory implements IFootNoteFactory {
		@Override
		public IFootNote createFootNote() {
			FootNote footNote = new FootNote();
			footNote.insert();
			return footNote;
		}
	}
}
