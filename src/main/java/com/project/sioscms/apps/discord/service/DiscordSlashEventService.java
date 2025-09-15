package com.project.sioscms.apps.discord.service;

import com.project.sioscms.SioscmsApplication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiscordSlashEventService {

    @Value("${discord.guild-key}")
    private String GUILD_KEY;

    private JDA getJDA() {
        return SioscmsApplication.getJda();
    }

    //region 가입신청 모달 이벤트
    public void slashUserJoinModalEvent(SlashCommandInteractionEvent event){
        Member member = event.getGuild().getMemberById(event.getUser().getId());
        if(member.getRoles().stream().anyMatch(r -> r.getId().equals("1125385136221995038"))){
            event.deferReply(true).queue();
            event.getHook().editOriginal("드라이버 역할이 부여된 멤버는 사용할 수 없는 기능입니다.").queue();
            return;
        }

        String uuid = "joinModal|" + event.getUser().getId() + "|" + System.nanoTime();
        //가입신청 입력 필드 생성(최대 4개)
        //가입사유, 닉네임, 플랫폼(Steam,EA[ps,xb]), 친구초대id(stream,ea)

        //가입사유
        TextInput joinNoteField = TextInput.create("JoinNoteField", "가입사유", TextInputStyle.PARAGRAPH)
                .setPlaceholder("가입사유를 간단하게 입력하세요. ex)F1 게임 입문해서 함께 즐기고 싶어요!")
                .setMaxLength(400)
                .setRequired(true)
                .build();

        //닉네임
        TextInput nickNameField = TextInput.create("NickNameField", "닉네임[한글사용불가]", TextInputStyle.SHORT)
                .setPlaceholder("디스코드 닉네임 입력(게임/디스코드/카카오 통일)")
                .setMinLength(2)
                .setMaxLength(100)
                .setRequired(true)
                .build();

        //플랫폼
        TextInput platFormField = TextInput.create("PlatFormField", "플랫폼[Steam or EA]", TextInputStyle.SHORT)
                .setPlaceholder("Steam / EA(플스/엑박) 중 입력(대소문자 무관)")
                .setMinLength(2)
                .setMaxLength(20)
                .setRequired(true)
                .build();

        //초대id
        TextInput inviteIdField = TextInput.create("InviteIdField", "초대 ID[Steam 친구 코드 또는 EA ID 입력]", TextInputStyle.SHORT)
                .setPlaceholder("PS, XBOX는 EA 홈페이지에서 확인")
                .setRequired(true)
                .build();

        Modal modal =  Modal.create(uuid, "가입 신청")
                .addActionRow(joinNoteField)
                .addActionRow(nickNameField)
                .addActionRow(platFormField)
                .addActionRow(inviteIdField)
                .build();
        event.replyModal(modal).queue();

    }
    //endregion
}
