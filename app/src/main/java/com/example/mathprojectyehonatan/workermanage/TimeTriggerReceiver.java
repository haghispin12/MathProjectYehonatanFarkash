package com.example.mathprojectyehonatan.workermanage;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.mathprojectyehonatan.R;
import com.google.firebase.firestore.FirebaseFirestore;

// ה-Receiver שלנו הוא "השליח" שמקבל הוראה מהמערכת ומבצע אותה ברקע
public class TimeTriggerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // יוצרים חיבור לבסיס הנתונים שלנו בענן (Firestore)
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // מבצעים שאילתה: "תביא לי את כל המסמכים באוסף 'workers' שבהם השדה 'isEntered' שווה ל-true"
        // שימוש ב-whereEqualTo חוסך לנו הורדת מידע מיותר וחוסך זמן סוללה
        db.collection("workers")
                .whereEqualTo("isEntered", true)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // ברגע שהשרת מחזיר תשובה בהצלחה, אנחנו בודקים כמה עובדים נמצאו ברשימה
                    int count = queryDocumentSnapshots.size();

                    // קוראים לפונקציה שמציגה את ההתראה למשתמש
                    showNotification(context, count);
                });
    }

    // פונקציית עזר לבניית ושליחת התראה למכשיר
    // פונקציה שמקבלת את ההקשר (Context) ואת מספר העובדים, ומציגה התראה למשתמש
    private void showNotification(Context context, int count) {

        // 1. הגדרת 'מזהה ערוץ' (Channel ID).
        // אנדרואיד מחייבת ערוץ כדי לנהל התראות. זהו "השם" של הערוץ שלנו במערכת.
        String channelId = "work_report_channel";

        // 2. הגדרת היעד (Intent). אנחנו אומרים למחשב: "כשלוחצים על ההתראה, תפתח את המסך שנקרא factorymanager"
        Intent intent = new Intent(context, factorymanager.class);

        // הגדרה שמוודאת שהמסך נפתח כ"חלון חדש" ולא "מתלבש" על מסך קיים, כדי למנוע תקלות.
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // 3. יצירת 'PendingIntent'. זה בעצם "כרטיס כניסה" למערכת של אנדרואיד.
        // אנחנו אומרים למערכת: "תשמרי את ה-Intent הזה אצלך, ותפעלי אותו רק אם המשתמש לוחץ על ההתראה".
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // 4. בניית ההתראה (Builder).
        // כאן אנחנו מעצבים את ההודעה: אייקון, כותרת, וטקסט.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground) // האייקון שיופיע למעלה בשורת המצב
                .setContentTitle("דיווח נוכחות")                 // הכותרת של ההודעה
                .setContentText("כרגע נמצאים במפעל " + count + " עובדים.") // הטקסט שמשתנה
                .setPriority(NotificationCompat.PRIORITY_DEFAULT) // חשיבות ההתראה
                .setAutoCancel(true)                              // סגירת ההתראה אוטומטית אחרי לחיצה
                .setContentIntent(pendingIntent);                 // החיבור ל'כרטיס הכניסה' שיצרנו בסעיף 3

        // 5. יצירת הערוץ (NotificationChannel).
        // זה קטע קוד חובה בכל גרסה של אנדרואיד מ-8 ומעלה. אם לא ניצור אותו, ההתראה לא תופיע.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "דיווחים", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // 6. שליחת ההתראה (Notify).
        // כאן אנחנו מבקשים מהמערכת להציג את ההתראה שבנינו.
        // אבל רגע! אנדרואיד החדשה מחייבת אותנו לבדוק אם יש לנו הרשאה.
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // בדיקה: "האם יש לנו רשות מהמשתמש להציג התראות?"
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            // אם כן - תציג! (1 זה ה-ID של ההתראה, כדי שנוכל לשנות אותה בעתיד)
            notificationManager.notify(1, builder.build());
        } else {
            // אם אין הרשאה - תדפיס הודעת שגיאה ב-Log כדי שנדע שחסרה הרשאה
            Log.e("NotificationError", "אין הרשאה להצגת התראות!");
        }
    }
}


