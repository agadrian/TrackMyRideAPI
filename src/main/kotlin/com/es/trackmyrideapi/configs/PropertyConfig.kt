package com.es.trackmyrideapi.configs

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration
@PropertySource("classpath:.env.properties")
class PropertyConfig