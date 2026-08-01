package com.codingshuttle.lovable_clone.service;

import com.codingshuttle.lovable_clone.dto.member.InviteMemberRequest;
import com.codingshuttle.lovable_clone.dto.member.MemberResponse;
import com.codingshuttle.lovable_clone.dto.member.UpdateMemberRoleRequest;

import java.util.List;

public interface ProjectMemberService {
    List<MemberResponse> getProjectMembers(Long projectId);

    MemberResponse inviteMember(Long projectId, InviteMemberRequest request);

    MemberResponse updateMemberRole(Long projectId, Long memberId, UpdateMemberRoleRequest request);

    Void removeProjectMember(Long projectId, Long memberId);
}
