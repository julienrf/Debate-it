package models;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class FootNote extends Model /*implements IFootNote*/ {
	
	/** Footnote HTML content */
	@Required @MaxSize(1000) // Play validator
	@Lob
	public String content;
	
	/** Parent post where this footnote is referenced */
	@Required
	@ManyToOne
	public Paragraph paragraph;
	
	
	public FootNote(final String content) {
		this.content = content;
	}
	
	/*@Override
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
	}*/
	
	
	/*public static class Factory implements IFootNoteFactory {
		@Override
		public FootNote createFootNote() {
			FootNote footNote = new FootNote();
			footNote.save();
			return footNote;
		}
	}*/
}
