package com.fsck.k9.storage.messages

import android.content.ContentValues
import app.k9mail.core.mail.folder.api.FolderDetails
import app.k9mail.legacy.mailstore.MoreMessages
import com.fsck.k9.mail.FolderClass
import com.fsck.k9.mail.FolderType
import com.fsck.k9.mailstore.LockableDatabase
import com.fsck.k9.mailstore.toDatabaseFolderType

internal class UpdateFolderOperations(private val lockableDatabase: LockableDatabase) {
    fun changeFolder(folderServerId: String, name: String, type: FolderType) {
        lockableDatabase.execute(false) { db ->
            val values = ContentValues().apply {
                put("name", name)
                put("type", type.toDatabaseFolderType())
            }

            db.update("folders", values, "server_id = ?", arrayOf(folderServerId))
        }
    }

    fun updateFolderSettings(folderDetails: FolderDetails) {
        lockableDatabase.execute(false) { db ->
            val contentValues = ContentValues().apply {
                put("top_group", folderDetails.isInTopGroup)
                put("integrate", folderDetails.isIntegrate)
                put("sync_enabled", folderDetails.isSyncEnabled)
                put("display_class", folderDetails.displayClass.name)
                put("notifications_enabled", folderDetails.isNotificationsEnabled)
                put("push_enabled", folderDetails.isPushEnabled)
            }

            db.update("folders", contentValues, "id = ?", arrayOf(folderDetails.folder.id.toString()))
        }
    }

    fun setIncludeInUnifiedInbox(folderId: Long, includeInUnifiedInbox: Boolean) {
        setBoolean(folderId, columnName = "integrate", value = includeInUnifiedInbox)
    }

    fun setDisplayClass(folderId: Long, folderClass: FolderClass) {
        setString(folderId = folderId, columnName = "display_class", value = folderClass.name)
    }

    fun setSyncEnabled(folderId: Long, enable: Boolean) {
        setBoolean(folderId = folderId, columnName = "sync_enabled", value = enable)
    }

    fun setPushEnabled(folderId: Long, enable: Boolean) {
        setBoolean(folderId = folderId, columnName = "push_enabled", value = enable)
    }

    fun setNotificationsEnabled(folderId: Long, enable: Boolean) {
        setBoolean(folderId, columnName = "notifications_enabled", value = enable)
    }

    fun setMoreMessages(folderId: Long, moreMessages: MoreMessages) {
        setString(folderId = folderId, columnName = "more_messages", value = moreMessages.databaseName)
    }

    fun setLastChecked(folderId: Long, timestamp: Long) {
        lockableDatabase.execute(false) { db ->
            val contentValues = ContentValues().apply {
                put("last_updated", timestamp)
            }

            db.update("folders", contentValues, "id = ?", arrayOf(folderId.toString()))
        }
    }

    fun setStatus(folderId: Long, status: String?) {
        setString(folderId = folderId, columnName = "status", value = status)
    }

    fun setVisibleLimit(folderId: Long, visibleLimit: Int) {
        lockableDatabase.execute(false) { db ->
            val contentValues = ContentValues().apply {
                put("visible_limit", visibleLimit)
            }

            db.update("folders", contentValues, "id = ?", arrayOf(folderId.toString()))
        }
    }

    fun setPushDisabled() {
        lockableDatabase.execute(false) { db ->
            val contentValues = ContentValues().apply {
                put("push_enabled", false)
            }

            db.update("folders", contentValues, null, null)
        }
    }

    private fun setString(folderId: Long, columnName: String, value: String?) {
        lockableDatabase.execute(false) { db ->
            val contentValues = ContentValues().apply {
                if (value == null) {
                    putNull(columnName)
                } else {
                    put(columnName, value)
                }
            }

            db.update("folders", contentValues, "id = ?", arrayOf(folderId.toString()))
        }
    }

    private fun setBoolean(folderId: Long, columnName: String, value: Boolean) {
        lockableDatabase.execute(false) { db ->
            val contentValues = ContentValues().apply {
                put(columnName, value)
            }

            db.update("folders", contentValues, "id = ?", arrayOf(folderId.toString()))
        }
    }
}
