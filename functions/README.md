# Match notifications

The Android app only registers its FCM installation and displays notifications.
This backend sends the host a fixed, localized notification when an existing
room changes from WAITING with no second player to TO_CONFIRM with two distinct
players. It reads the current room again to discard stale events and sends only
when the host's presence is BACKGROUND. The registration is read from the
existing default Firestore collection fcmTokens (fid preferred, token fallback).
No new Firestore database, collection, index, or schema is required.

The function uses the Cloud Functions runtime identity (Application Default
Credentials). Do not download or deploy a service-account JSON file.

## Deployment

1. Use Node.js 22 and run `npm ci` and `npm test` in this directory.
2. From the repository root, authenticate the Firebase CLI with an authorized
   account: `npx firebase-tools login`.
3. Verify the existing default Firestore database is in Native mode and supports
   the existing document API. It could not be inspected during this change
   because the local Firebase CLI was not authenticated.
4. Check the live Realtime Database rules: room creation must bind player1Id to
   auth.uid; joining must bind player2Id to auth.uid, preserve player1Id, and
   allow only a WAITING -> TO_CONFIRM transition. Users must not impersonate
   other players' presence. Only a user may write their own fcmTokens document.
   Device registrations should no longer be readable by other clients.
   Live rules are not present in this repository and were not changed.
5. Give the function runtime service account access to read the existing Realtime
   Database and Firestore registrations, and permission to send FCM messages
   (Firebase Cloud Messaging API Admin). Enable the required Firebase/Functions
   APIs and billing for Cloud Functions.
6. Run from the root:
   `npx firebase-tools deploy --only functions:notifications --project gravitygame-446320`
   Supply MATCH_DATABASE_INSTANCE (instance name, not URL) and
   MATCH_DATABASE_REGION (the region of that existing Realtime Database).
   Firebase CLI stores these non-secret parameters in a gitignored .env file.
7. Test with two accounts: create a waiting room, background the host, and join
   as the second player. Check the localized notification and room link. Also
   verify that a foreground host and unrelated room writes produce no push.
8. Deploy the backend before releasing the updated Android client. Until it is
   deployed, the updated client does not send match notifications.

Events have at-least-once delivery semantics. A stable Android notification tag
replaces an existing notification for the same room; it does not guarantee
exactly-once FCM delivery. Automatic retries are disabled to avoid repeating
time-sensitive invitations. FCM TTL is one minute; monitor function errors.

## Previously distributed credentials

If a real key was ever included in an APK/AAB or committed, revoke that key in
Google Cloud IAM -> Service Accounts -> Keys and review its use in audit logs.
Removing it from the current source or adding an ignore rule cannot revoke it
from older releases or Git history. No credential file was found in the current
assets directory, and no cloud key has been revoked by this change.

References:
- https://firebase.google.com/docs/functions/database-events
- https://firebase.google.com/docs/cloud-messaging/send/admin-sdk
