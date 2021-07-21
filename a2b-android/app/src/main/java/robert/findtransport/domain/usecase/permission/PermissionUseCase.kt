package robert.findtransport.domain.usecase.permission

interface PermissionUseCase {
  fun hasPermission(permission: String): Boolean
}