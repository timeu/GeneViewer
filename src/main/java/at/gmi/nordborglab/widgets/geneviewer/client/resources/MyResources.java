package at.gmi.nordborglab.widgets.geneviewer.client.resources;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;


public interface MyResources extends ClientBundle  {

	public interface MainStyle extends CssResource {
		String popupPanel();
		String popupRowEven();
		String popupNameRow();
		String geneviewer();
		String settingsPopup();
		String settingsButton();
		String settingsContent();
		String settingsContentTitle();
		//String settingsContentItemChecked();
		String settingsContentItemCheckboxChecked();
		String settingsContentItem();
		String settingsContentItemText();
		String settingsContentItemCheckbox();
    }
	
	@Source("style.css")
	MainStyle style();
	
	@Source("checkmark.png")
    ImageResource checkmark();
	
	@Source("settings_icon.png")
    ImageResource settings_icon();
}
