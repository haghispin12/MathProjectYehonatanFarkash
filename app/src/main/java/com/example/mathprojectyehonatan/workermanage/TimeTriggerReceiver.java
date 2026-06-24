package com.example.mathprojectyehonatan.workermanage;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.mathprojectyehonatan.R;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * בעצם כשמגיע הזמן שמוגדר בAlarmManager
 * הוא מפעיל את מה שה PendingIntent מצביע עליו שהוא זה BroadcastReceiver
 */
// ה-Receiver שלנו הוא "השליח" שמקבל הוראה מהמערכת ומבצע אותה ברקע
public class TimeTriggerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("DEBUG_LOG", "ה-Receiver הופעל!"); // שורה לבדיקה
        String today = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(new java.util.Date());
        // יוצרים חיבור לבסיס הנתונים (Firestore)
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // כאן אנחנו שולפים את המספר שהדבקנו ב-Intent
        String factoryNumber = intent.getStringExtra("factoryNumber");

        if (factoryNumber != null) {
            // מבצעים שאילתה: "תביא לי את כל המסמכים באוסף 'workers' שבהם השדה 'isEntered' שווה ל-true"
            // שימוש ב-whereEqualTo חוסך לנו הורדת מידע מיותר וחוסך זמן סוללה
            // סיננו גם לפי factoryNumber כדי לקבל רק את העובדים של המנהל הנוכחי
            db.collection("workers")
                    .whereEqualTo("factoryNumber", factoryNumber)
                    .whereEqualTo("isEntered", true)
                    .whereEqualTo("entryDate", today)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        // ברגע שהשרת מחזיר תשובה בהצלחה, אנחנו בודקים כמה עובדים נמצאו ברשימה
                        int count = queryDocumentSnapshots.size();

                        // קוראים לפונקציה שמציגה את ההתראה למשתמש
                        showNotification(context, count);
                        scheduleNextAlarm(context, factoryNumber, 10, 10);
                        scheduleNextAlarm(context, factoryNumber, 18, 18);
                    });

        }
    }

    /**
     * * מתזמנת את ההתראה הבאה להפעלה ביום הבא בשעה נתונה.
     *  * הפעולה מחשבת את זמן ההפעלה, מכינה את ה-PendingIntent הנדרש, ומבצעת רישום ב-AlarmManager
     *  * כדי להבטיח ביצוע מדויק גם כאשר המכשיר במצב שינה (Doze Mode).
     *  * בגרסאות אנדרואיד מתקדמות (S ומעלה), הפעולה מוודאת קיום הרשאות תזמון לפני קביעת האזעקה.
     *  *
     *  * @param context       הקשר האפליקציה (Context)
     *  * @param factoryNumber מספר המפעל להעברה ל-Receiver
     *  * @param hour          השעה ביום (0-23) שבה תופעל ההתראה
     *  * @param requestCode   מזהה ייחודי עבור ה-PendingIntent
     *  */
    private void scheduleNextAlarm(Context context, String factoryNumber, int hour, int requestCode) {
        // קבלת גישה לשירות ה-AlarmManager של מערכת האנדרואיד
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // יצירת Intent שמצביע על ה-Receiver הנוכחי שלנו כדי שיופעל שוב
        Intent intent1 = new Intent(context, TimeTriggerReceiver.class);

        // העברת מספר המפעל כ"מטען" (Extra) כדי שה-Receiver ידע איזה נתונים לשלוף מחר
        intent1.putExtra("factoryNumber", factoryNumber);

        // יצירת PendingIntent ("שלט רחוק") שמאפשר למערכת להפעיל את הקוד שלנו מאוחר יותר בזמנו
        // משתמשים ב-FLAG_UPDATE_CURRENT כדי לעדכן את המידע אם הוא השתנה
        PendingIntent pi = PendingIntent.getBroadcast(context, requestCode, intent1, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        // קבלת מופע של לוח השנה הנוכחי כדי לחשב את זמן ההפעלה הבא
        java.util.Calendar cal = java.util.Calendar.getInstance();

        // הגדרת התאריך ליום הבא (כדי שההתראה תהיה תקפה ליום שאחרי)
        cal.add(java.util.Calendar.DATE, 1);

        // הגדרת השעה המבוקשת (לפי הפרמטר שהעברנו לפונקציה)
        cal.set(java.util.Calendar.HOUR_OF_DAY, hour);

        // איפוס הדקות והשניות ל-0 כדי שההתראה תהיה מדויקת בתחילת השעה
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);

        // בדיקה שה-AlarmManager זמין ולא ריק
        if (am != null) {
            // קביעת התראה מדויקת שפועלת גם אם המכשיר במצב שינה (Doze Mode)
            // זה מבטיח שההתראה תמיד תגיע בזמן
            // בודקים אם אפשר לתזמן התראה מדויקת
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (am.canScheduleExactAlarms()) {
                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
                } else {
                    showPermissionNotification(context);
                }
            } else {
                // בגרסאות ישנות אין צורך בבדיקה הזו
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
            }
        }
    }

    // פונקציית עזר לבניית ושליחת התראה למכשיר
    // פונקציה שמקבלת את ההקשר (Context) ואת מספר העובדים, ומציגה התראה למשתמש
    private void showNotification(Context context, int count) {

        // 1. הגדרת 'מזהה ערוץ' (Channel ID).
        // אנדרואיד מחייבת ערוץ כדי לנהל התראות. זהו "השם" של הערוץ שלנו במערכת.
        String channelId = "final_test_channel_2026";

        // 2. הגדרת היעד (Intent). אנחנו אומרים למחשב: "כשלוחצים על ההתראה, תפתח את המסך שנקרא factorymanager"
        Intent intent = new Intent(context, factorymanager.class);

        // הגדרה שמוודאת שהמסך נפתח כ"חלון חדש" ולא "מתלבש" על מסך קיים, כדי למנוע תקלות.
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // יצירת 'PendingIntent'. זה בעצם "כרטיס כניסה" למערכת של אנדרואיד.
        // אנחנו אומרים למערכת: "תשמרי את ה-Intent הזה אצלך, ותפעלי אותו רק אם המשתמש לוחץ על ההתראה".
// הוספנו | PendingIntent.FLAG_UPDATE_CURRENT למניעת קריסה באנדרואיד חדש
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        // 4. בניית ההתראה (Builder).
        // כאן אנחנו מעצבים את ההודעה: אייקון, כותרת, וטקסט.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground) // האייקון שיופיע למעלה בשורת המצב
                .setContentTitle("דיווח נוכחות")                 // הכותרת של ההודעה
                .setContentText("כרגע נמצאים במפעל " + count + " עובדים.") // הטקסט שמשתנה
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)                              // סגירת ההתראה אוטומטית אחרי לחיצה
                .setContentIntent(pendingIntent);                 // החיבור ל'כרטיס הכניסה' שיצרנו בסעיף 3

        // 5. יצירת הערוץ (NotificationChannel).
        // זה קטע קוד חובה בכל גרסה של אנדרואיד מ-8 ומעלה. אם לא ניצור אותו, ההתראה לא תופיע.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "דיווחים", NotificationManager.IMPORTANCE_HIGH);
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
    // פונקציה שמציגה התראה שקוראת למשתמש להפעיל הרשאות
    private void showPermissionNotification(Context context) {
        // שלב א': יצירת Intent שפותח את מסך ההגדרות הספציפי של מערכת ההפעלה לאפליקציה שלנו
        Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
        // שלב ב': 'עטיפת' ה-Intent בתוך כרטיס כניסה למערכת (PendingIntent)
        // הוספנו | PendingIntent.FLAG_UPDATE_CURRENT למניעת קריסה באנדרואיד חדש
        // תיקון שם המשתנה ל-pi כדי שיתאים לשורה שמפעילה את ההתראה בהמשך
        PendingIntent pi = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        // שלב ג': בניית ההתראה עם כותרת וטקסט שמסבירים למשתמש למה אנחנו צריכים את זה
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "permission_channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("דרושה הרשאה!")
                .setContentText("לחץ כאן כדי להפעיל את התראות הנוכחות.")
                .setContentIntent(pi) // ברגע שהמשתמש ילחץ על ההתראה, יפתח מסך ההגדרות
                .setAutoCancel(true); // סוגר את ההתראה אחרי הלחיצה

        // שלב ד': שליחת ההתראה למערכת
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            manager.notify(2, builder.build());
        }
    }
}


