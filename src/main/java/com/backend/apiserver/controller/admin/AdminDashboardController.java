package com.backend.apiserver.controller.admin;

import com.backend.apiserver.bean.response.DashboardResponse;
import com.backend.apiserver.service.DashboardService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Secured("ROLE_ADMIN")
@RequestMapping("api/admin/dashboard")
public class AdminDashboardController {

	private DashboardService dashboardService;

	@GetMapping
	public DashboardResponse getDashboardInf() {
		return dashboardService.getDashboardInfo();
	}
}
