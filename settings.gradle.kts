rootProject.name = "e-commerce"

include("common")
include("common:cache")
include("common:client")
include("common:lock")
include("common:message")
include("common:outbox")
include("common:querydsl")
include("common:serialize")
include("common:storage")

include("service")
include("service:balance")
include("service:coupon")
include("service:order")
include("service:payment")
include("service:product")
include("service:user")

include("support")
include("support:rest-docs")
