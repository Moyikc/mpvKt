package live.mehiz.mpvkt.ui.player.controls.components

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import live.mehiz.mpvkt.R
import live.mehiz.mpvkt.databinding.PlayerDoubleTapSeekViewBinding

/**
 * View that shows the arrows animation when double tapping to seek
 * Originally from https://github.com/aniyomiorg/aniyomi
 * Thanks @Quickdesh for allowing me to use it
 */
class DoubleTapSeekSecondsView(
  context: Context,
  showSeekIcon: Boolean,
  showSeekTime: Boolean,
  attrs: AttributeSet?
) : LinearLayout(context, attrs) {

  var binding: PlayerDoubleTapSeekViewBinding

  companion object {
    const val ICON_ANIMATION_DURATION = 750L
  }

  var cycleDuration: Long = ICON_ANIMATION_DURATION
    set(value) {
      firstAnimator.duration = value / 5
      secondAnimator.duration = value / 5
      thirdAnimator.duration = value / 5
      fourthAnimator.duration = value / 5
      fifthAnimator.duration = value / 5
      field = value
    }

  var seconds: Int = 0
    set(value) {
      binding.doubleTapSeconds.text = context.getString(
        R.string.player_seek_n_seconds,
        value
      )
      field = value
    }

  var isForward: Boolean = true
    set(value) {
      binding.triangleContainer.rotation = if (value) 0f else 180f
      field = value
    }

  @DrawableRes
  var icon: Int = R.drawable.ic_play_seek_triangle
    set(value) {
      if (value > 0) {
        binding.tri1.setImageResource(value)
        binding.tri2.setImageResource(value)
        binding.tri3.setImageResource(value)
      }
      field = value
    }

  init {
    binding = PlayerDoubleTapSeekViewBinding.inflate(LayoutInflater.from(context), this)
    orientation = VERTICAL
    layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    binding.triangleContainer.visibility = if (showSeekIcon) View.VISIBLE else View.GONE
    binding.doubleTapSeconds.visibility = if (showSeekTime) View.VISIBLE else View.GONE
  }

  fun start() {
    stop()
    firstAnimator.start()
  }

  fun stop() {
    firstAnimator.cancel()
    secondAnimator.cancel()
    thirdAnimator.cancel()
    fourthAnimator.cancel()
    fifthAnimator.cancel()

    reset()
  }

  private fun reset() {
    binding.tri1.alpha = 0f
    binding.tri2.alpha = 0f
    binding.tri3.alpha = 0f
  }

  private val firstAnimator: ValueAnimator = CustomValueAnimator(
    {
      binding.tri1.alpha = 0f
      binding.tri2.alpha = 0f
      binding.tri3.alpha = 0f
    },
    {
      binding.tri1.alpha = it
    },
    {
      secondAnimator.start()
    },
  )

  private val secondAnimator: ValueAnimator = CustomValueAnimator(
    {
      binding.tri1.alpha = 1f
      binding.tri2.alpha = 0f
      binding.tri3.alpha = 0f
    },
    {
      binding.tri2.alpha = it
    },
    {
      thirdAnimator.start()
    },
  )

  private val thirdAnimator: ValueAnimator = CustomValueAnimator(
    {
      binding.tri1.alpha = 1f
      binding.tri2.alpha = 1f
      binding.tri3.alpha = 0f
    },
    {
      binding.tri1.alpha = 1f - binding.tri3.alpha
      binding.tri3.alpha = it
    },
    {
      fourthAnimator.start()
    },
  )

  private val fourthAnimator: ValueAnimator = CustomValueAnimator(
    {
      binding.tri1.alpha = 0f
      binding.tri2.alpha = 1f
      binding.tri3.alpha = 1f
    },
    {
      binding.tri2.alpha = 1f - it
    },
    {
      fifthAnimator.start()
    },
  )

  private val fifthAnimator: ValueAnimator = CustomValueAnimator(
    {
      binding.tri1.alpha = 0f
      binding.tri2.alpha = 0f
      binding.tri3.alpha = 1f
    },
    {
      binding.tri3.alpha = 1f - it
    },
    {
      firstAnimator.start()
    },
  )

  private inner class CustomValueAnimator(
    start: () -> Unit,
    update: (value: Float) -> Unit,
    end: () -> Unit,
  ) : ValueAnimator() {

    init {
      duration = cycleDuration / 5
      setFloatValues(0f, 1f)

      addUpdateListener { update(it.animatedValue as Float) }
      addListener(
        object : AnimatorListener {
          override fun onAnimationStart(animation: Animator) {
            start()
          }

          override fun onAnimationEnd(animation: Animator) {
            end()
          }

          override fun onAnimationCancel(animation: Animator) = Unit

          override fun onAnimationRepeat(animation: Animator) = Unit
        },
      )
    }
  }
}
