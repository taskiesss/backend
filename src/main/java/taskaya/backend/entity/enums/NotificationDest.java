package taskaya.backend.entity.enums;

public enum NotificationDest {

    PROPOSAL, //{send prop to client ,acceptance , rejection}

    CONTRACT, //{new offer for freelancers ✅, acceptance(client and freelancers) , rejection(client) ,milestone review req to client ,send milestone approval to freelancer  }

    COMMUNITY_PROFILE, //{to freelancer after join acceptance }

    COMMUNITY_JOBS_AND_TALENTS, //{if there is a new join request (appears for community admin only)}

    COMMUNITY_POSTS, //{new post , comments }

    COMMUNITY_SETTINGS, //{new settings (for all members )}

}
