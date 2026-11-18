"use strict";

function isPlayerId(value) {
  return typeof value === "string" && value.trim().length > 0 &&
    !/[.#$\[\]\/]/.test(value);
}

/**
 * Only a waiting room becoming a match can produce a notification.
 * No client-supplied recipient, text, or device token is accepted.
 */
async function notifyOpponentFound(before, after, roomId, services) {
  if (!before || !after || before.status !== "WAITING" ||
      after.status !== "TO_CONFIRM" || before.player2Id ||
      !isPlayerId(after.player1Id) || !isPlayerId(after.player2Id) ||
      before.player1Id !== after.player1Id ||
      after.player1Id === after.player2Id) return false;

  // Events may arrive after the room was deleted, replaced, or confirmed.
  const current = await services.readRoom(roomId);
  if (!current || current.status !== "TO_CONFIRM" ||
      current.player1Id !== after.player1Id ||
      current.player2Id !== after.player2Id) return false;
  if (await services.readPlayerState(after.player1Id) !== "BACKGROUND") return false;

  const registration = await services.readRegistration(after.player1Id);
  const nonBlank = value => typeof value === "string" && value.trim().length > 0;
  const recipient = nonBlank(registration?.fid) ? { fid: registration.fid }
    : nonBlank(registration?.token) ? { token: registration.token } : null;
  if (!recipient) return false;

  await services.send({
    ...recipient,
    data: { roomId, type: "OPPONENT_FOUND" },
    android: {
      priority: "high",
      ttl: 60_000,
      collapseKey: "opponent_found",
      notification: {
        channelId: "default_channel",
        tag: `opponent_found_${roomId}`,
        titleLocKey: "notificationTitleOpponentFound",
        bodyLocKey: "notificationBodyOpponentFound",
      },
    },
  });
  return true;
}

module.exports = { notifyOpponentFound };
