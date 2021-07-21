package robert.findtransport.utils.extensions

import robert.findtransport.R
import robert.findtransport.base.BaseFragment
import robert.findtransport.utils.BACK_STACK

fun <Fragment : BaseFragment<*, *>> BaseFragment<*, *>.replaceWithSlide(
  target: Fragment,
  container: Int = R.id.frContainer,
  addToBackStack: Boolean = false,
  popBack: Boolean = true,
) = activity?.run {
  supportFragmentManager
    .beginTransaction()
    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_in_left, R.anim.slide_out_left)
    .replace(container, target)
    .apply {
      if (addToBackStack) {
        if (popBack && supportFragmentManager.backStackEntryCount > 0) {
          supportFragmentManager.popBackStack()
        }
        addToBackStack(BACK_STACK)
      }
    }
    .commitAllowingStateLoss()
}

fun <Fragment : BaseFragment<*, *>> BaseFragment<*, *>.replaceWithAlpha(target: Fragment, container: Int = R.id.frContainer) =
  activity?.run {
    supportFragmentManager
      .beginTransaction()
      .setCustomAnimations(R.anim.alpha_in, R.anim.alpha_out)
      .replace(container, target)
      .commitAllowingStateLoss()
  }

fun <Fragment : BaseFragment<*, *>> BaseFragment<*, *>.add(target: Fragment, container: Int = R.id.frContainer) = activity?.run {
  supportFragmentManager
    .beginTransaction()
    .setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up, R.anim.slide_in_down, R.anim.slide_out_down)
    .add(container, target)
    .addToBackStack(BACK_STACK)
    .commitAllowingStateLoss()
}

fun <Fragment : BaseFragment<*, *>> BaseFragment<*, *>.addWithSlide(target: Fragment, container: Int = R.id.frContainer) = activity?.run {
  supportFragmentManager
    .beginTransaction()
    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_in_left, R.anim.slide_out_left)
    .add(container, target)
    .addToBackStack(BACK_STACK)
    .commitAllowingStateLoss()
}
