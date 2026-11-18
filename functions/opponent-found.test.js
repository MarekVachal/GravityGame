"use strict";

const { test } = require("node:test");
const assert = require("node:assert/strict");
const { notifyOpponentFound } = require("./opponent-found");

const before = { status: "WAITING", player1Id: "host", player2Id: "" };
const after = { ...before, status: "TO_CONFIRM", player2Id: "guest" };

function fixture(overrides = {}) {
  const messages = [];
  const services = {
    readRoom: async () => after,
    readPlayerState: async uid => {
      assert.equal(uid, "host");
      return "BACKGROUND";
    },
    readRegistration: async uid => {
      assert.equal(uid, "host");
      return { fid: "installation", token: "old-token" };
    },
    send: async message => messages.push(message),
    ...overrides,
  };
  return { messages, services };
}

test("notifies the host with a fixed localized message and a room link", async () => {
  const { services, messages } = fixture();
  assert.equal(await notifyOpponentFound(before, after, "room-1", services), true);
  assert.equal(messages.length, 1);
  assert.equal(messages[0].fid, "installation");
  assert.equal(messages[0].token, undefined);
  assert.deepEqual(messages[0].data, { roomId: "room-1", type: "OPPONENT_FOUND" });
  assert.equal(messages[0].android.ttl, 60_000);
  assert.equal(messages[0].android.notification.titleLocKey, "notificationTitleOpponentFound");
  assert.equal(messages[0].android.notification.channelId, "default_channel");
});

test("ignores unrelated writes, missing rooms and malformed participants without reading tokens", async () => {
  const { services, messages } = fixture({
    readRegistration: async () => assert.fail("must not read tokens"),
  });
  for (const [oldRoom, newRoom] of [
    [null, after],
    [before, null],
    [after, after],
    [before, { ...after, status: "FULL" }],
    [before, { ...after, player1Id: "changed-host" }],
    [before, { ...after, player2Id: "host" }],
    [before, { ...after, player2Id: "" }],
    [before, { ...after, player2Id: "../invalid" }],
  ]) {
    assert.equal(await notifyOpponentFound(oldRoom, newRoom, "room-1", services), false);
  }
  assert.equal(messages.length, 0);
});

test("ignores stale events and foreground or absent players", async () => {
  for (const overrides of [
    { readRoom: async () => null },
    { readRoom: async () => ({ ...after, status: "FULL" }) },
    { readRoom: async () => ({ ...after, player2Id: "other-guest" }) },
    { readPlayerState: async () => "FOREGROUND" },
    { readPlayerState: async () => null },
  ]) {
    const { services, messages } = fixture({
      readRegistration: async () => assert.fail("must not read tokens"),
      ...overrides,
    });
    assert.equal(await notifyOpponentFound(before, after, "room-1", services), false);
    assert.equal(messages.length, 0);
  }
});

test("supports a legacy registration and skips missing or blank registrations", async () => {
  for (const registration of [undefined, {}, { fid: "", token: " " }]) {
    const { services, messages } = fixture({ readRegistration: async () => registration });
    assert.equal(await notifyOpponentFound(before, after, "room-1", services), false);
    assert.equal(messages.length, 0);
  }
  const { services, messages } = fixture({ readRegistration: async () => ({ token: "legacy" }) });
  await notifyOpponentFound(before, after, "room-1", services);
  assert.equal(messages[0].token, "legacy");
});

test("propagates provider failures to Cloud Functions error reporting", async () => {
  const failure = new Error("FCM unavailable");
  const { services } = fixture({ send: async () => { throw failure; } });
  await assert.rejects(notifyOpponentFound(before, after, "room-1", services), failure);
});
