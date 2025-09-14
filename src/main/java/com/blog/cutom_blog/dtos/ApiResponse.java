package com.blog.cutom_blog.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true
)
public class ApiResponse<Typ> {
    public String message;
    public String errorCode;
    public Typ data;
    private Map<String, String> meta;

    protected ApiResponse() {
    }

    public ApiResponse(String message, String errorCode, Typ data, Map<String, String> meta) {
        this.message = message;
        this.errorCode = errorCode;
        this.data = data;
        this.meta = meta;
    }

    public ApiResponse(String message, Typ data) {
        this.message = message;
        this.data = data;
        this.meta = null;
    }

    public ApiResponse(String message, String errorCode) {
        this.message = message;
        this.errorCode = errorCode;
        this.meta = null;
        this.data = null;
    }

    public ApiResponse<Typ> addPaginationMeta(long currentPage, int pageSize, long totalPages, long totalRows) {
        if (this.meta == null) {
            this.meta = new HashMap();
        }

        this.meta.put("currentPage", String.valueOf(currentPage));
        this.meta.put("pageSize", String.valueOf(pageSize));
        this.meta.put("totalPages", String.valueOf(totalPages));
        this.meta.put("totalRows", String.valueOf(totalRows));
        return this;
    }

    public static <Typ> ApiResponseBuilder<Typ> builder() {
        return new ApiResponseBuilder<Typ>();
    }

    public String getMessage() {
        return this.message;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public Typ getData() {
        return this.data;
    }

    public Map<String, String> getMeta() {
        return this.meta;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public void setData(Typ data) {
        this.data = data;
    }

    public void setMeta(Map<String, String> meta) {
        this.meta = meta;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof ApiResponse)) {
            return false;
        } else {
            ApiResponse<?> other = (ApiResponse)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$message = this.getMessage();
                Object other$message = other.getMessage();
                if (this$message == null) {
                    if (other$message != null) {
                        return false;
                    }
                } else if (!this$message.equals(other$message)) {
                    return false;
                }

                Object this$errorCode = this.getErrorCode();
                Object other$errorCode = other.getErrorCode();
                if (this$errorCode == null) {
                    if (other$errorCode != null) {
                        return false;
                    }
                } else if (!this$errorCode.equals(other$errorCode)) {
                    return false;
                }

                Object this$data = this.getData();
                Object other$data = other.getData();
                if (this$data == null) {
                    if (other$data != null) {
                        return false;
                    }
                } else if (!this$data.equals(other$data)) {
                    return false;
                }

                Object this$meta = this.getMeta();
                Object other$meta = other.getMeta();
                if (this$meta == null) {
                    if (other$meta != null) {
                        return false;
                    }
                } else if (!this$meta.equals(other$meta)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof ApiResponse;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $message = this.getMessage();
        result = result * 59 + ($message == null ? 43 : $message.hashCode());
        Object $errorCode = this.getErrorCode();
        result = result * 59 + ($errorCode == null ? 43 : $errorCode.hashCode());
        Object $data = this.getData();
        result = result * 59 + ($data == null ? 43 : $data.hashCode());
        Object $meta = this.getMeta();
        result = result * 59 + ($meta == null ? 43 : $meta.hashCode());
        return result;
    }

    public String toString() {
        String var10000 = this.getMessage();
        return "ApiResponse(message=" + var10000 + ", errorCode=" + this.getErrorCode() + ", data=" + this.getData() + ", meta=" + this.getMeta() + ")";
    }

    public static class ApiResponseBuilder<Typ> {
        private String message;
        private String errorCode;
        private Typ data;
        private Map<String, String> meta;

        ApiResponseBuilder() {
        }

        public ApiResponseBuilder<Typ> message(String message) {
            this.message = message;
            return this;
        }

        public ApiResponseBuilder<Typ> errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public ApiResponseBuilder<Typ> data(Typ data) {
            this.data = data;
            return this;
        }

        public ApiResponseBuilder<Typ> meta(Map<String, String> meta) {
            this.meta = meta;
            return this;
        }

        public ApiResponse<Typ> build() {
            return new ApiResponse<Typ>(this.message, this.errorCode, this.data, this.meta);
        }

        public String toString() {
            return "ApiResponse.ApiResponseBuilder(message=" + this.message + ", errorCode=" + this.errorCode + ", data=" + this.data + ", meta=" + this.meta + ")";
        }
    }
}
