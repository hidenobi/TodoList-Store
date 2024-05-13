package com.padi.todo_list.ui.detail_task

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.padi.todo_list.R
import com.padi.todo_list.common.base.BaseBindingActivity
import com.padi.todo_list.common.utils.IntentConstants
import com.padi.todo_list.common.utils.UtilsJ
import com.padi.todo_list.databinding.ActivityNoteBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteActivity() :
    BaseBindingActivity<NoteViewModel, ActivityNoteBinding>(R.layout.activity_note) {
    companion object {
        const val ACTION_UPDATE_NOTE = "ACTION_UPDATE_NOTE"
        const val ACTION_UPDATE_USED_CREATED_NOTE = "ACTION_UPDATE_USED_CREATED_NOTE"
        fun start(
            context: Context,
            titleNote: String?,
            idTask: Long,
            usedCreateNote: Boolean
        ) {
            val intent = Intent(context, NoteActivity::class.java).apply {
                putExtra(IntentConstants.idTask, idTask)
                putExtra(IntentConstants.titleNote, titleNote)
                putExtra(IntentConstants.usedCreatedNote, usedCreateNote)
            }
            context.startActivity(intent)
        }
    }
    override val viewModel: NoteViewModel by viewModels()
    var idTask: Long = 0
    private var usedCreatedNote: Boolean = false
    private var titleTask: String = ""
    override fun initView(savedInstanceState: Bundle?) {
        UtilsJ.hideNavigationBar(window)
        UtilsJ.setStatusBar(window, R.color.main_screen, true)
        idTask = intent.getLongExtra(IntentConstants.idTask, 0)
        usedCreatedNote = intent.getBooleanExtra(IntentConstants.usedCreatedNote, false)
        titleTask = intent.getStringExtra(IntentConstants.titleNote) ?: ""
        viewModel.getNote(idTask)
        viewModel.eventNoteLiveData.observe(this@NoteActivity) {
            viewBinding.event = it.apply {
                titLe = if (!usedCreatedNote) titleTask else titLe
            }
        }
        viewBinding.tbNote.handleAction(leftAction = {
            updateNote()
            finish()
        })
    }

    override fun onResume() {
        if (!usedCreatedNote) viewBinding.titleNote.setText(titleTask)
        super.onResume()
    }

    private fun updateNote() {
        viewBinding.apply {
            if (
                contentNote.text.isNotEmpty() &&
                (titleNote.text.toString().trim() != titleTask.trim() ||
                        usedCreatedNote ||
                        titleNote.text.isNotEmpty())
            ) {
                viewModel.addOrUpdateNote(
                    titleNote.text.toString(),
                    contentNote.text.toString(),
                    idTask
                )
            } else {
                viewModel.deleteNote(idTask)
            }
        }
    }

    override fun onPause() {
        updateNote()
        super.onPause()
    }
    override fun onDestroy() {
        sendUpdateNoteIntent()
        super.onDestroy()
    }

    private fun sendUpdateNoteIntent() {
        val intent = Intent(this@NoteActivity, DetailTaskActivity::class.java).apply {
            action = ACTION_UPDATE_NOTE
        }
        LocalBroadcastManager.getInstance(this@NoteActivity).sendBroadcast(intent)
    }
}