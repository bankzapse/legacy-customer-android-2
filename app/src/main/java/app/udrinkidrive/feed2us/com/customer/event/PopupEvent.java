package app.udrinkidrive.feed2us.com.customer.event;

/**
 * Created by TL3 on 3/7/2017 AD.
 */

public class PopupEvent {

    private String url_popup;

    public PopupEvent(String url_popup) {
        this.url_popup = url_popup;
    }

    public String getUrl_popup() {
        return url_popup;
    }
}
