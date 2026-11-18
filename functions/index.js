"use strict";

const { initializeApp } = require("firebase-admin/app");
const { getFirestore } = require("firebase-admin/firestore");
const { getMessaging } = require("firebase-admin/messaging");
const { onValueUpdated } = require("firebase-functions/v2/database");
const { defineString } = require("firebase-functions/params");
const { notifyOpponentFound } = require("./opponent-found");

initializeApp(); // Runtime identity (ADC); no private key file.
const databaseInstance = defineString("MATCH_DATABASE_INSTANCE");
const databaseRegion = defineString("MATCH_DATABASE_REGION");

exports.notifyOpponentFound = onValueUpdated(
  {
    ref: "/rooms/{roomId}",
    instance: databaseInstance,
    region: databaseRegion,
    retry: false,
    maxInstances: 5,
  },
  async event => {
    const root = event.data.after.ref.root;
    await notifyOpponentFound(
      event.data.before.val(),
      event.data.after.val(),
      event.params.roomId,
      {
        readRoom: async roomId => (await root.child("rooms").child(roomId).get()).val(),
        readPlayerState: async uid =>
          (await root.child("players").child(uid).child("state").get()).val(),
        readRegistration: async uid =>
          (await getFirestore().collection("fcmTokens").doc(uid).get()).data(),
        send: message => getMessaging().send(message),
      },
    );
  },
);
